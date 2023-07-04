package dev.kingrabbit.fruitfulutilities.hud.elements;

import dev.kingrabbit.fruitfulutilities.hud.ElementInfo;
import dev.kingrabbit.fruitfulutilities.hud.HudElement;
import dev.kingrabbit.fruitfulutilities.hud.Serializable;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@ElementInfo(id = "auction_timer")
public class AuctionTimerElement extends HudElement {

    @Serializable(id = "x")
    public int x = 5;

    @Serializable(id = "y")
    public int y = 5;

    @Override
    public List<Object> render(float tickDelta) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        // Get the current minute and second
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);

        // Calculate the time remaining until the next 30-minute interval
        int minutesRemaining = 30 - (currentMinute % 30) - 1;
        int secondsRemaining = 60 - currentSecond;
        if (secondsRemaining == 60) {
            minutesRemaining++;
            secondsRemaining = 0;
        }

        return Collections.singletonList("§6Auction Timer: §e" + (minutesRemaining < 10 ? "0" : "") + minutesRemaining + ":" + (secondsRemaining < 10 ? "0" : "") + secondsRemaining);
    }

}
