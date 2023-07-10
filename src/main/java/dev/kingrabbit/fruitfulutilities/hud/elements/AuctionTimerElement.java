package dev.kingrabbit.fruitfulutilities.hud.elements;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.AuctionTimerCategory;
import dev.kingrabbit.fruitfulutilities.hud.ElementInfo;
import dev.kingrabbit.fruitfulutilities.hud.HudElement;
import dev.kingrabbit.fruitfulutilities.hud.Serializable;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings("unused")
@ElementInfo(id = "auction_timer")
public class AuctionTimerElement extends HudElement {

    @Serializable(id = "x")
    public int x = 5;

    @Serializable(id = "y")
    public int y = 5;

    @Override
    public List<Object> render(float tickDelta) {
        if (!FruitfulUtilities.getInstance().configManager.getCategory(AuctionTimerCategory.class).enabled) return Collections.emptyList();

        int[] timeUntilAuctions = getTimeUntilAuctions();
        int minutesRemaining = timeUntilAuctions[0], secondsRemaining = timeUntilAuctions[1];

        return Collections.singletonList("§6Auction Timer: §e" + (minutesRemaining < 10 ? "0" : "") + minutesRemaining + ":" + (secondsRemaining < 10 ? "0" : "") + secondsRemaining);
    }

    public static int[] getTimeUntilAuctions() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);

        int minutesRemaining = 30 - (currentMinute % 30) - 1;
        int secondsRemaining = 60 - currentSecond;
        if (secondsRemaining == 60) {
            minutesRemaining++;
            secondsRemaining = 0;
        }

        return new int[]{minutesRemaining, secondsRemaining};
    }

}
