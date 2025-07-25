package com.totemfletching;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import java.util.List;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
class TotemFletchingOverlay extends Overlay {
    private final Client client;
    private final TotemFletchingPlugin plugin;

    private final Color tileColor = new Color(0, 255, 0, 100);
    private final Color borderColor = new Color(0, 255, 0, 200);

    @Inject
    private TotemFletchingOverlay(Client client, TotemFletchingPlugin plugin, TotemFletchingConfig config) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return null;
        }

        List<EntTrail> objects = plugin.getTrails();
        if (objects.isEmpty())
        {
            return null;
        }

        Stroke stroke = new BasicStroke(2);
        for (EntTrail trail : objects) {
            GameObject object = trail.getTileObject();
            if (object.getPlane() != client.getPlane())
            {
                continue;
            }

            ObjectComposition composition = trail.getComposition();
            if (composition.getImpostorIds() != null)
            {
                // This is a multiloc
                composition = composition.getImpostor();
                // Only mark the object if the name still matches
                if (composition == null
                        || Strings.isNullOrEmpty(composition.getName())
                        || "null".equals(composition.getName())
                        || !composition.getName().equals(trail.getName()))
                {
                    continue;
                }
            }

            final LocalPoint objectPosLocal = object.getLocalLocation();
            Polygon tilePoly = Perspective.getCanvasTilePoly(client, objectPosLocal);
            if (tilePoly != null)
            {
                OverlayUtil.renderPolygon(graphics, tilePoly, borderColor, tileColor, stroke);
            }


        }

        return null;
    }
}
