package dev.kingrabbit.fruitfulutilities.hud;

import java.util.List;

@SuppressWarnings("unused")
public abstract class HudElement {

    public abstract List<Object> render(float tickDelta);

}
