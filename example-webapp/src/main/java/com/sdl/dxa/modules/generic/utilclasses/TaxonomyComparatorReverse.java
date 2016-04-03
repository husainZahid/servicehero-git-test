package com.sdl.dxa.modules.generic.utilclasses;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh
 * Date: 1/4/12
 * Time: 10:58 AM
 */

public class TaxonomyComparatorReverse implements Comparator {
    public int compare(Object taxonomy1, Object taxonomy2) {
        String taxonomyDescription1 = ((com.tridion.taxonomies.Keyword)taxonomy1).getKeywordName();
        String taxonomyDescription2 = ((com.tridion.taxonomies.Keyword)taxonomy2).getKeywordName();
        return taxonomyDescription2.compareTo(taxonomyDescription1);
    }
}
