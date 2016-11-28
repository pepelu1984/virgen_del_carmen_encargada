package com.dynamicdroides.virgendelcarmen.comedor.data;

import com.dynamicdroides.virgendelcarmen.service.data.Behavior;

/**
 * Created by noel on 9/1/16.
 */
public class BehaviorData
{

    public String type;
    public String value;

    public BehaviorData(String type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public static BehaviorData importFrom(Behavior behavior)
    {
        return new BehaviorData(behavior.getTipo(), behavior.getValor());
    }

}
