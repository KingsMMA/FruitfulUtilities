package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@CategoryInfo(id = "message_hider", display = "Message Hider")
public class MessageHiderCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enable the Message Hider category.")
    public boolean enabled = true;

    @ConfigBoolean(id = "no_super_melons", display = "Disable No Super Melons message", description = "Disables the \"You don't have any Super Enchanted Melons.\" message from the Merchant.")
    public boolean noSuperMelons = true;

    @ConfigBoolean(id = "no_increased_security", display = "Disable Increase Security messages", description = "Disables the \"You don't have any Super Enchanted Melons.\" message from the Merchant.")
    public boolean increasedSecurity = true;

}
