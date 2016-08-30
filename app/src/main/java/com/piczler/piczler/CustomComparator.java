package com.piczler.piczler;

import java.util.Comparator;

/**
 * Created by matiyas on 7/12/16.
 */
public class CustomComparator implements Comparator<GettersAndSetters> {
    @Override
    public int compare(GettersAndSetters o1, GettersAndSetters o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
