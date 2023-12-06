package me.blueslime.minedis.extension.cache;

import me.blueslime.minedis.extension.utils.StaffStatus;
import me.blueslime.minedis.modules.cache.Cache;

import java.util.Map;
import java.util.UUID;

public class StaffCache extends Cache<UUID, StaffStatus> {
    public StaffCache(Map<UUID, StaffStatus> initialMap) {
        super(initialMap);
    }
}
