package com.fwloopins.tabby.client.mixin;

import com.fwloopins.tabby.client.TabbyClient;
import com.fwloopins.tabby.client.config.TabbyConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
	@Shadow @Final private MinecraftClient client;
	@Shadow @Nullable private Text footer;
	@Shadow @Nullable private Text header;
	@Unique TabbyConfig config = AutoConfig.getConfigHolder(TabbyConfig.class).getConfig();

	@ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
	private long modifyCount(long count) {
		if (config.general.maxCount <= 0 && client.getNetworkHandler() != null)
			return client.getNetworkHandler().getListedPlayerListEntries().size();

		return config.general.maxCount;
	}

	@ModifyConstant(constant = @Constant(intValue = 20), method = "render")
	private int modifyMaxRows(int MAX_ROWS) {
		if (!config.general.adaptive)
			return Math.max(1, config.general.maxRows);

		if (config.general.maxCount <= 0 && client.getNetworkHandler() != null) {
			int onlinePlayers = client.getNetworkHandler().getListedPlayerListEntries().size();
			return Math.max(1, onlinePlayers / config.general.adaptiveDivisor);
		}

		return (int) Math.max(1, config.general.maxCount / config.general.adaptiveDivisor);
	}

	@Inject(method = "setHeader", at = @At("TAIL"))
	private void setHeader(Text header, CallbackInfo ci) {
		if (!config.misc.customHeader.isEmpty()) {
			if (config.misc.customHeader.equals("null")) {
				this.header = null;
			} else {
				this.header = Text.of(config.misc.customHeader);
			}
		}
	}

	@Inject(method = "setFooter", at = @At("TAIL"))
	private void setFooter(Text footer, CallbackInfo ci) {
		if (!config.misc.customFooter.isEmpty()) {
			if (config.misc.customFooter.equals("null")) {
				this.footer = null;
			} else {
				this.footer = Text.of(config.misc.customFooter);
			}
		}
	}
}
