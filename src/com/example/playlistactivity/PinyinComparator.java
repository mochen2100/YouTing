package com.example.playlistactivity;

import java.util.Comparator;

import de.greenrobot.daoexample.Music;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<Music> {

	@Override
	public int compare(Music lhs, Music rhs) {
		// TODO Auto-generated method stub
		return 0;
	}

/*	public int compare(Music o1, Music o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}*/

}
