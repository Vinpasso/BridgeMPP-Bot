package bots.ProductPlacementBot;


public class Mix {
	
	public static void mergeMix(Object[] array) {
		if (array.length < 2) return;
		mergeMix(array, 0, array.length - 1);
	}
	
	private static void mergeMix (Object[] array, int l, int r) {
		if (l == r) return;
		int m = (r + l) / 2;
		mergeMix(array, l, m);
		mergeMix(array, m + 1, r);
		int j = l;
		int k = m + 1;
		Object[] array2 = new Object[r - l + 1];
		//merge
		for (int i = 0; i <= r - l; i++) {
			//empty left part
			if (j > m) {
				array2[i] = array[k];
				k++;
			}
			//empty right part
			else if (k > r) {
				array2[i] = array[j];
				j++;
			}
			else if (2 * Math.random() < 1.0) {
				array2[i] = array[j];
				j++;
			}
			else {
				array2[i] = array[k];
				k++;
			}
		}
		for (int i = 0; i <= r - l; i++) {
			array[l + i] = array2[i];
		}
	}
	
	public static void identityMix () {
		
	}
}
