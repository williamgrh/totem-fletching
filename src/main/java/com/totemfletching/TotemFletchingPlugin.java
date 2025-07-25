package com.totemfletching;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.Getter;
import lombok.AccessLevel;

@Slf4j
@PluginDescriptor(
	name = "Vale Totems"
)
public class TotemFletchingPlugin extends Plugin
{
	public static final int ENT_TRAIL_FIRST = 57115;
	public static final int ENT_TRAIL_SECOND = 57116;
	public static final int SPIRIT_BUFFALO = 14589;
	public static final int SPIRIT_JAGUAR = 14590;
	public static final int SPIRIT_EAGLE = 14591;
	public static final int SPIRIT_SNAKE = 14592;
	public static final int SPIRIT_SCORPION = 14593;

	@Getter(AccessLevel.PACKAGE)
	private static final List<EntTrail> trails = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private TotemFletchingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TotemFletchingOverlay overlay;

	@Provides
	TotemFletchingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TotemFletchingConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		trails.clear();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		var object = event.getGameObject();
		if (object.getId() == ENT_TRAIL_FIRST || object.getId() == ENT_TRAIL_SECOND) {
			ObjectComposition composition = getObjectComposition(object.getId());
			String name = composition.getName();
			trails.add(new EntTrail(object, composition, name));
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		trails.removeIf(o -> o.getTileObject() == event.getGameObject());
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
		trails.removeIf(o -> o.getTileObject().getWorldLocation().equals(playerPos));
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		GameState gameState = gameStateChanged.getGameState();
		if (gameState == GameState.LOADING)
		{
			// Reload points with new map regions
			trails.clear();
		}
	}

	private ObjectComposition getObjectComposition(int id)
	{
		ObjectComposition objectComposition = client.getObjectDefinition(id);
		return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
	}
}
