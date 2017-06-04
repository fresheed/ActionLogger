package org.fresheed.actionlogger.classification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fresheed on 03.06.17.
 */

public enum ActivityKind {
    WALK(2),
    PUSHUPS(3),
    SITS(0),
    TYPING(1);

    private static final Map<Integer, ActivityKind> id_mapping=new HashMap<Integer, ActivityKind>(){{
        for (ActivityKind kind: ActivityKind.values()){
            put(kind.getValue(), kind);
        }
    }};

    private final int id;

    ActivityKind(int id){
        this.id=id;
    }

    public int getValue(){
        return id;
    }

    public static ActivityKind getKindById(int id) {
        if (!id_mapping.containsKey(id)){
            throw new IllegalArgumentException("Invalid activity id given: "+id);
        }
        return id_mapping.get(id);
    }
}
