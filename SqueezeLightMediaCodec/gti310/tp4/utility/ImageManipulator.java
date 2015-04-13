package gti310.tp4.utility;

import java.util.ArrayList;
import java.util.Arrays;

import gti310.tp4.Entropy;
import gti310.tp4.PPMReaderWriter;
import gti310.tp4.SZLReaderWriter;

public class ImageManipulator implements IConstants{


	/** Fonction qui convertis une image RGB en format YCbCr
	 * @param image	le array de int en 3 dimensions représentant l'image
	 * @return	yCbCbImageResult	Array 3 dimensions de float représentant l'image convertie en YCbCr
	 */
	public static float[][][] RGBToYCbCrImageConversion(int[][][] image){
		float[][][] yCbCrImageResult = new float[image.length][image[0].length][image[0][0].length];
		float [] tempYCbCrResult = {0,0,0};

		for (int i = 0; i < image[0].length; i++) { // on boucle sur le contenu de la seconde dimension du array()  normalement -> [0-256] 
			for (int j = 0; j < image[0][i].length; j++) { // on boucle sur le contenu de la troisiemme dimension -> [0-256]
				tempYCbCrResult = Converter.RGBToYCbCr(image[R][i][j], image[G][i][j], image[B][i][j]);
				yCbCrImageResult[R][i][j] = tempYCbCrResult[R];
				yCbCrImageResult[G][i][j] = tempYCbCrResult[G];
				yCbCrImageResult[B][i][j] = tempYCbCrResult[B];
			}
		}
		return yCbCrImageResult;
	}

	/** Fonction qui convertis une image YCbCb en format RGB
	 * @param image	le array de float en 3 dimensions représentant l'image
	 * @return	RGBImageResult	Array 3 dimensions de int représentant l'image convertie en YCbCr
	 */
	public static int[][][] YCbCrToRGBImageConversion(float[][][] image){
		int[][][] RGBImageResult = new int[image.length][image[0].length][image[0][0].length];
		int[] tempRGBResult = {0,0,0};

		for (int i = 0; i < image[0].length; i++) { // on boucle sur le contenu de la seconde dimension du array()  normalement -> [0-256] 
			for (int j = 0; j < image[0][i].length; j++) { // on boucle sur le contenu de la troisiemme dimension -> [0-256]
				tempRGBResult = Converter.YCbCrToRGB(image[Y][i][j], image[Cb][i][j], image[Cr][i][j]);
				RGBImageResult[Y][i][j] = tempRGBResult[Y];
				RGBImageResult[Cb][i][j] = tempRGBResult[Cb];
				RGBImageResult[Cr][i][j] = tempRGBResult[Cr];
			}
		}
		return RGBImageResult;
	}

	/**
	 * Fonction qui applique un traitement DCT sur un array donné
	 * @param originalArray array 3 dimension [R,G,B][0-256][0-256]
	 * @return ResultDctArray le résultat de la conversion
	 */
	public static float[][][] DCTConversion(float[][][] originalArray){
		float[][] tempDctArray = new float[BLOCK_SIZE][BLOCK_SIZE];
		float[][] resultDctArray = new float[BLOCK_SIZE][BLOCK_SIZE];
		float[][][] returnedDctArray = new float[COLOR_SPACE_SIZE][256][256];
		int nbBlock = originalArray[0].length/BLOCK_SIZE;
		int valX = 0;
		int valY = 0;

		for (int i = 0 ; i < COLOR_SPACE_SIZE; i++) { // boucler a travers les composantes
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							valX = h * BLOCK_SIZE + x;
							valY = v * BLOCK_SIZE + y;
							tempDctArray[y][x] = originalArray[i][valY][valX];
						}
					}
					// -> la dct retourne des valeurs et il faut les ajouter au array resultat
					// les valeurs doivent êtres ajoutés a la suites des autres afin de retourner 
					// array resultat complet a la fin 
					resultDctArray = Converter.DCTConverter(tempDctArray);

					for (int k = 0; k < BLOCK_SIZE; k++) {
						for (int l = 0;  l < BLOCK_SIZE; l++) {
							valX = h * BLOCK_SIZE + l;
							valY = v * BLOCK_SIZE + k;
							returnedDctArray[i][valY][valX] = resultDctArray[k][l];
						}
					}
				}
			}
		}

		return returnedDctArray;
	}

	/**
	 * Fonction qui effectue la DCT inverse
	 * @param originalArray	un array 3 dimensions de float
	 * @return	resultDctArray	Le array résultat de la DCT en 3 dimensions 
	 */
	public static float[][][] IDCTConversion(float[][][] originalArray){
		float[][] tempDctArray = new float[BLOCK_SIZE][BLOCK_SIZE];
		float[][][] resultDctArray = new float[COLOR_SPACE_SIZE][BLOCK_SIZE][BLOCK_SIZE];
		int nbBlock = originalArray[0].length/BLOCK_SIZE;

		for (int i = 0 ; i < originalArray.length; i++) { // boucler a travers les composantes
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							int valX = h * BLOCK_SIZE + x;
							int valY = v * BLOCK_SIZE + y;

							tempDctArray[y][x] = originalArray[i][valY][valX];
						}
					}
					// effectuer la conversion avec la fonction de DCT
					resultDctArray[i] = Converter.IDCTConverter(tempDctArray);
				}
			}
		}
		return resultDctArray;
	}
	
	/**
	 * Fonction qui a pour but de trouver la valeur du alpha utilisé dans la quantification 
	 * @param facteurQualite	un int entre 0 et 100 qui donne la qualité
	 * @return	alpha	La valeur du alpha selon la qualite
	 */
	public static float findAlpha(int facteurQualite){
		float alpha = -5;

		if((facteurQualite >= 1) && (facteurQualite <= 50)){
			alpha = 50 / facteurQualite;
		}
		else{
			alpha = ((200.0f - (2.0f * facteurQualite)) / 100.0f );
		}
		return alpha;
	}

	
	/**
	 * Fonction qui effectue l'operation de quantification sur le array resultant de la dct 
	 * @param DCTArray	Le resultat de l'operation de dct
	 * @param facteurQualite	Le facteur de qualité voulu, entré par l'user a l'appel du programme
	 * @return	arrayQuantifie	Le array de la dct après la quantification
	 */
	public static int[][][] quantification(float[][][] DCTArray, int facteurQualite){

		int[][][] arrayQuantifie = new int[COLOR_SPACE_SIZE][DCTArray[0].length][DCTArray[0].length]; // -> 3x256x256
		float alpha = -1;
		int nbBlock = DCTArray[0].length/BLOCK_SIZE;
		int valX = 0;
		int valY = 0;

		if(facteurQualite == 100){ // pas de changements
			for (int j = 0; j < DCTArray[0].length; j++) {
				for (int k = 0; k < DCTArray[0][0].length; k++) {
					arrayQuantifie[Y][j][k] = (int) Math.round(DCTArray[Y][j][k]);
					arrayQuantifie[Cb][j][k] = (int) Math.round(DCTArray[Cb][j][k]);
					arrayQuantifie[Cr][j][k] = (int) Math.round(DCTArray[Cr][j][k]);
				}
			}
		}
		else{ // si facteur de qualite est autre que 100
			alpha = findAlpha(facteurQualite);
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							valX = h * BLOCK_SIZE + x;
							valY = v * BLOCK_SIZE + y;
							arrayQuantifie[Y][valY][valX] = (int) Math.round(DCTArray[Y][valY][valX] / (alpha * QuantificationQyTable[y][x]));
							arrayQuantifie[Cb][valY][valX] = (int) Math.round(DCTArray[Cb][valY][valX] / (alpha * QuantificationQcbQcrTable[y][x]));
							arrayQuantifie[Cr][valY][valX] = (int) Math.round(DCTArray[Cr][valY][valX] / (alpha * QuantificationQcbQcrTable[y][x]));
						}
					}
				}
			}
		}
		return arrayQuantifie;
	}
	

	/**
	 * Fonction qui effectue la quantification inverse sur le array quantifié.
	 * @param arrayQuantifie	Le array quantifié
	 * @param facteurQualite	Le facteur de qualité voulu
	 * @return	arrayQuantifieInverse	Le array déquantifié
	 */
	public static float[][][] reverseQuantification(int[][][] arrayQuantifie, int facteurQualite){
	
		float[][][] arrayQuantifieInverse = new float[COLOR_SPACE_SIZE][arrayQuantifie[0].length][arrayQuantifie[0].length];
		float alpha = -1;
		int nbBlock = arrayQuantifie[0].length/8;
		int valX = 0;
		int valY = 0;
	
		if(facteurQualite == 100){ // pas de changements
			for (int j = 0; j < arrayQuantifie[0].length; j++) {
				for (int k = 0; k < arrayQuantifie[0][0].length; k++) {
					arrayQuantifieInverse[Y][j][k] =  Math.round(arrayQuantifie[Y][j][k]);
					arrayQuantifieInverse[Cb][j][k] = Math.round(arrayQuantifie[Cb][j][k]);
					arrayQuantifieInverse[Cr][j][k] = Math.round(arrayQuantifie[Cr][j][k]);
				}
			}
		}
		else{ // si facteur de qualite est autre que 100
			alpha = findAlpha(facteurQualite);
			
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							valX = h * BLOCK_SIZE + x;
							valY = v * BLOCK_SIZE + y;
							arrayQuantifieInverse[Y][valY][valX] = (float) arrayQuantifie[Y][valY][valX] * (alpha * QuantificationQyTable[y][x]);
							arrayQuantifieInverse[Cb][valY][valX] = (float) arrayQuantifie[Cb][valY][valX] * (alpha * QuantificationQcbQcrTable[y][x]);
							arrayQuantifieInverse[Cr][valY][valX] = (float) arrayQuantifie[Cr][valY][valX] * (alpha * QuantificationQcbQcrTable[y][x]);
						}
					}
				}
			}
		}

		return arrayQuantifieInverse;
	}


	/**
	 * Fonction qui sers a effectuer le traitement de parcours Zig-Zag afin de rassemble les zéros ensembles le plus possible
	 * @param QuantificationResult	Le résultat de la quantification
	 * @return	returnedZigZagArray	Le array
	 */
	public static int[][][][] zigzagger(int[][][] QuantificationResult){

		int valX = 0;
		int valY = 0;
		int nbBlock = QuantificationResult[0].length/8;
		int[][] tempZigZagArray = new int[nbBlock][nbBlock];
		int[][][][] returnedZigZagArray = new int[COLOR_SPACE_SIZE][nbBlock][nbBlock][BLOCK_SIZE*BLOCK_SIZE];
		
		for (int i = 0 ; i < COLOR_SPACE_SIZE; i++) { // boucler a travers les composantes
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							valX = h * BLOCK_SIZE + x;
							valY = v * BLOCK_SIZE + y;
	
							tempZigZagArray[y][x] = QuantificationResult[i][valY][valX];
						}
					}
					returnedZigZagArray[i][v][h] = Zig_Zag(tempZigZagArray);
				}
			}
		}
		return returnedZigZagArray;
	}
	
	
	/**
	 * Traverser un tableau 8x8 et retourner les valeurs sous forme de array 1 dimension
	 * @param array
	 * @return
	 * Inspiré depuis http://rosettacode.org/wiki/Zig-zag_matrix
	 */
	public static int[] Zig_Zag(int[][] array){
		int size = BLOCK_SIZE;
		int[] data = new int[64];
		int i = 1;
		int j = 1;
	
		for (int element = 0; element < 64; element++){
			data[element] = array[i - 1][j - 1];
	
			if ((i + j) % 2 == 0){ // Even stripes
				if (j < size){
					j++;
				}
				else{
					i+= 2;
				}
				if (i > 1){
					i--;
				}
			}
			else{// Odd stripes
				if (i < size){
					i++;
				}
				else{
					j+= 2;
				}
				if (j > 1){
					j--;
				}
			}
		}
		return data;
	}
	
	
	/**
	 * Fonction qui sers a effectuer le traitement de parcours Zig-Zag afin de rassemble les zéros ensembles le plus possible
	 * @param QuantificationResult	Le résultat de la quantification
	 * @return	returnedZigZagArray	Le array
	 */
	public static int[][][][] reverseZigzagger(int[][][][] ZigZagArray){
// pas fonctionnel
		int valX = 0;
		int valY = 0;
		int nbBlock = ZigZagArray[0].length;
		int index = 0;
		int[][] tempZigZagArray = new int[nbBlock][nbBlock];
		int[][][][] returnedZigZagArray = new int[COLOR_SPACE_SIZE][nbBlock][nbBlock][BLOCK_SIZE*BLOCK_SIZE];
		
		for (int i = 0 ; i < COLOR_SPACE_SIZE; i++) { // boucler a travers les composantes
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					for (int y = 0; y < BLOCK_SIZE; y++) {
						for (int x = 0; x < BLOCK_SIZE ; x++) {
							valX = h * BLOCK_SIZE + x;
							valY = v * BLOCK_SIZE + y;
							
							tempZigZagArray[y][x] = ZigZagArray[i][v][h][index];
							index++;
						}
					}
					returnedZigZagArray[i][v][h] = Zig_Zag(tempZigZagArray);
				}
			}
		}
		return returnedZigZagArray;
	}
	
	
	/**
	 * Traverser un tableau 8x8 et retourner les valeurs sous forme de array 1 dimension
	 * @param array
	 * @return
	 * Inspiré depuis http://rosettacode.org/wiki/Zig-zag_matrix
	 */
	public static int[][] reverseZig_Zag(int[] array){
		//pas fonctionnel
		int size = BLOCK_SIZE;
		int[][] data = new int[8][8];
		int i = 1;
		int j = 1;
	
		for (int element = 0; element < 64; element++){
			data[i - 1][j-1] = array[element];
			//	data[element] = array[i - 1][j - 1];
	
			if ((i + j) % 2 == 0){ // Even stripes
				if (j < size){
					j++;
				}
				else{
					i+= 2;
				}
				if (i > 1){
					i--;
				}
			}
			else{// Odd stripes
				if (i < size){
					i++;
				}
				else{
					j+= 2;
				}
				if (j > 1){
					j--;
				}
			}
		}
		return data;
	}

	
	
	public static void entropyCoding(int[][][][] ZigZagArray, String fileName){
		
		int[][][][] entropyArray = DCDPCM(ZigZagArray);

		// on passe a l'écriture. 
		// on appele la fonction qui renvoie le bloc actuel de AC encodé en RLC
		//on écris le DC 
		// on écris les AC trouvés
		
		int nbBlock = 32;
		
		ArrayList<int[]> RLEValues = new ArrayList<int[]>();
		
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> 32
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> 32
					for (int i = 0; i < COLOR_SPACE_SIZE; i++) { // boucler sur les couleurs
						RLEValues = ACRLE(ZigZagArray[i][v][h]);
						
						// écrire les valeurs
						Entropy.writeDC(entropyArray[i][v][h][0]);
						
						for (int j = 0; j < RLEValues.size(); j++) {
							Entropy.writeAC(RLEValues.get(j)[0], RLEValues.get(j)[1]);
						}
					}
				}
			}
			
			SZLReaderWriter.writeSZLFile(fileName, 256,256,50);
			System.out.println("IMAGE ECRITE");
		
	}
	
	
	/**
	 * Fonction qui crée le array DC a partir du array resultant du ZigZag
	 * @param ZigZagArray
	 * @return returnedDCArray	Le array issu du traitement DC
	 */
	public static int[][][][] DCDPCM(int[][][][] ZigZagArray){

		int nbBlock = ZigZagArray[0].length;
		for (int i = 0 ; i < ZigZagArray.length; i++) { // boucler a travers les composantes
			for (int v = 0; v < nbBlock; v++) { // boucler a travers les blocs verticaux identifiés -> Y
				
				int actuel = -0;
				int precedent = 0;
				for (int h = 0; h < nbBlock; h++) { // boucler a travers les blocs horizontaux -> X
					precedent = actuel;
					actuel = ZigZagArray[i][v][h][0];
					ZigZagArray[i][v][h][0] = actuel-precedent;
				}
			}
		}
		return ZigZagArray;
	}

	
	/**
	 * Fonction qui crée un array AC a partir du array résultant du ZigZag
	 * @param ZigZagArray
	 * @return
	 */
	public static ArrayList<int[]> ACRLE(int[] block){

		int valeur = 0;
		int nbZeros = 0;
		int[] couple = new int[2];
		ArrayList<int[]> ar = new ArrayList<int[]>();
		
		ar.clear();
		valeur = 0;
		
		for (int l = 1; l < BLOCK_SIZE * BLOCK_SIZE-1; l++) { // boucler sur la longueur -> 64
			valeur = block[l];
			if(valeur != 0){
				// il faut absolument ré-initialiser le array couple car il est passé en référence au arraylist
				// et ainsi a chaque ajout le contenu complet de l'arraylist change pour le nouveau couple
				// http://www.coderanch.com/t/580391/java/java/Vector-object-overwritten-adding-element
				couple = new int[2];
				couple[0] = nbZeros;
				couple[1] = valeur;
		
				ar.add(couple);
				nbZeros = 0;
			}
			else{
				nbZeros++;
			}
			
			if(l == ((BLOCK_SIZE * BLOCK_SIZE)-2)){
				couple = new int[2];
				couple[0] = nbZeros;
				couple[1] = 0;
		
				ar.add(couple);
				nbZeros = 0;
			}
		}
		return ar;
	}
	
	
	
//**     WRITER     ** \\					

	public static void writeRGBFile(String path, int[][][] intArray){
		PPMReaderWriter.writePPMFile(path, intArray);
	}
	
	/*
	 * Function to write a file
	 */
	public static void writeYCbCrFile(String path, float[][][] floatArray){
		PPMReaderWriter.writePPMFile(path, YCbCrToRGBImageConversion(floatArray));
	}


}

