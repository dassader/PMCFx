package com.home;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileCache <T extends Serializable> extends AbstractValueAdaptingCache {
    public static final String CACHE_FILE_NAME = "cache.bin";
    private String name;

    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected FileCache(boolean allowNullValues, String name) {
        super(allowNullValues);
        this.name = name;
    }

    private HashMap<Object, T> readCacheMap() {
        File file = new File("." + File.separator + CACHE_FILE_NAME);

        if (!file.exists()) {
            HashMap<Object, T> cacheMap = new HashMap<>();
            writeCacheMap(cacheMap);
            return cacheMap;
        }

        InputStream resourceAsStream = null;
        try {
            resourceAsStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return (HashMap<Object, T>) SerializationUtils.deserialize(resourceAsStream);
    }

    private void writeCacheMap(HashMap<Object, T> cacheMap) {
        File file = new File("."+File.separator+CACHE_FILE_NAME);

        try {
            if(!file.exists()) {
                file.createNewFile();
            }

            SerializationUtils.serialize(cacheMap, new BufferedOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            throw new IllegalStateException("Fail create cache file!", e);
        }
    }

    @Override
    protected T lookup(Object key) {
        Map<Object, T> cacheMap = readCacheMap();

        return cacheMap.get(key);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public void put(Object key, Object value) {
        HashMap<Object, T> cacheMap = readCacheMap();

        cacheMap.put(key, (T) value);

        writeCacheMap(cacheMap);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        HashMap<Object, T> cacheMap = readCacheMap();

        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, (T) value);
            return null;
        }

        return new SimpleValueWrapper(cacheMap.get(key));
    }

    @Override
    public void evict(Object key) {
        HashMap<Object, T> cacheMap = readCacheMap();

        cacheMap.remove(key);

        writeCacheMap(cacheMap);
    }

    @Override
    public void clear() {
        writeCacheMap(new HashMap<>());
    }
}
