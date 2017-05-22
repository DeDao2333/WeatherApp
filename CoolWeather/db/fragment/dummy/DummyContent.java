package com.example.java.weatherapps.CoolWeather.db.fragment.dummy;

import com.example.java.weatherapps.CoolWeather.db.City;
import com.example.java.weatherapps.CoolWeather.db.County;
import com.example.java.weatherapps.CoolWeather.db.Province;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static void initProvinceList(List<Province> provinces){
        // Add some sample items.
        ITEMS.clear();
        for (Province province : provinces) {
            DummyItem dummyItem = new DummyItem(
                    String.valueOf(province.getProvinceCode()),
                    province.getProvinceName(), "");
            ITEMS.add(dummyItem);
        }
    }

    public static void initCityList(List<City> cities){
        // Add some sample items.
        ITEMS.clear();
        for (City city:cities) {
            DummyItem dummyItem = new DummyItem(
                    city.getCityCode(),
                    city.getCityName(), city.getProvinceId());
            ITEMS.add(dummyItem);
        }
    }

    public static void initCountyList(List<County> counties){
        // Add some sample items.
        ITEMS.clear();
        for (County county:counties) {
            DummyItem dummyItem = new DummyItem(
                    county.getWeatherId(),
                    county.getCountyName(),county.getCityId());
            ITEMS.add(dummyItem);
        }
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String preId;


        public DummyItem(String id, String content, String preId) {
            this.id = id;
            this.content = content;
            this.preId = preId;
        }



        @Override
        public String toString() {
            return content;
        }
    }
}
