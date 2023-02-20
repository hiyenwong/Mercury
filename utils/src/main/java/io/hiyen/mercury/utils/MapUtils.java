package io.hiyen.mercury.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Hash map 工具类
 *
 * @author Hi Yen Wong
 * @date 2023/1/13 16:51
 */
public class MapUtils<T> {

    public static class MapUtilsInstance<T> {
        private static final MapUtils<?> INSTANCE = new MapUtils<>();
    }

    public static MapUtils getInstance() {
        return MapUtilsInstance.INSTANCE;
    }

    /**
     * Hashmap 过滤器， 只表流列表中的数据
     *
     * @param mapData     需要过滤的hashmap
     * @param columnNames 保留字段
     * @return hashmap
     */
    public Map<String, T> fetch(Map<String, T> mapData, List<String> columnNames) {
        return filter(mapData, columnNames, true);
    }

    /**
     * 不保留 columnName 中所key的数据
     *
     * @param mapData
     * @param columnNames
     * @return 干净的 Map
     */
    public Map<String, T> clean(HashMap<String, T> mapData, List<String> columnNames) {
        return filter(mapData, columnNames, false);
    }

    /**
     * 过滤器
     *
     * @param mapData     需要过滤的数据
     * @param columnNames 针对的Key List
     * @param isOut       是否保留
     * @return HashMap  <String, T> 返回结果</String,>
     */
    public Map<String, T> filter(Map<String, T> mapData, List<String> columnNames,
                                     boolean isOut) {
        Iterator<Map.Entry<String, T>> iterator = mapData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, T> entry = iterator.next();
            String key = entry.getKey();
            if (columnNames.contains(key) == isOut) {
                iterator.remove();
            }
        }
        return mapData;
    }
}
