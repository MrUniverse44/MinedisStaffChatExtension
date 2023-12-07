package me.blueslime.minedis.extension.staffchat.cache;

import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import me.blueslime.minedis.modules.cache.Cache;

import java.util.Map;
import java.util.UUID;

public class StaffCache extends Cache<UUID, StaffStatus> {
    public StaffCache(Map<UUID, StaffStatus> initialMap) {
        super(initialMap);
    }
}
