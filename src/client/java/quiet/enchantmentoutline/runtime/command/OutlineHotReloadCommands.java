package quiet.enchantmentoutline.runtime.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import quiet.enchantmentoutline.runtime.orchestration.OutlineRenderOrchestrator;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;

/**
 * Lightweight dev commands for hot-reloading outline mode/settings in-game.
 */
public final class OutlineHotReloadCommands {
    private OutlineHotReloadCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var root = ClientCommandManager.literal("eo");

            root.then(ClientCommandManager.literal("mode")
                    .then(ClientCommandManager.argument("mode", StringArgumentType.word())
                            .executes(context -> {
                                String mode = StringArgumentType.getString(context, "mode");
                                OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                                orchestrator.setTechniqueMode(mode);
                                String active = OutlineTechniqueManager.getInstance().getCurrentMode().id();
                                context.getSource().sendFeedback(Component.literal("[EO] mode set to: " + active));
                                return 1;
                            })));

            root.then(ClientCommandManager.literal("radius")
                    .then(ClientCommandManager.argument("pixels", IntegerArgumentType.integer(1, 128))
                            .executes(context -> {
                                int pixels = IntegerArgumentType.getInteger(context, "pixels");
                                OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                                orchestrator.setOutlineRadiusPixels(pixels);
                                int activeRadius = orchestrator.currentSettings().outlineRadiusPixels();
                                context.getSource().sendFeedback(Component.literal("[EO] radius set to: " + activeRadius));
                                return 1;
                            })));

            var colorNode = ClientCommandManager.literal("color");
            var colorR = ClientCommandManager.argument("r", FloatArgumentType.floatArg(0.0F, 1.0F));
            var colorG = ClientCommandManager.argument("g", FloatArgumentType.floatArg(0.0F, 1.0F));
            var colorB = ClientCommandManager.argument("b", FloatArgumentType.floatArg(0.0F, 1.0F));
            var colorMix = ClientCommandManager.argument("mix", FloatArgumentType.floatArg(0.0F, 1.0F));

            colorB.executes(context -> {
                float r = FloatArgumentType.getFloat(context, "r");
                float g = FloatArgumentType.getFloat(context, "g");
                float b = FloatArgumentType.getFloat(context, "b");
                OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                orchestrator.setOutlineColor(r, g, b, 1.0F);
                context.getSource().sendFeedback(Component.literal("[EO] color set to: (" + r + ", " + g + ", " + b + "), mix=1.0"));
                return 1;
            });

            colorMix.executes(context -> {
                float r = FloatArgumentType.getFloat(context, "r");
                float g = FloatArgumentType.getFloat(context, "g");
                float b = FloatArgumentType.getFloat(context, "b");
                float mix = FloatArgumentType.getFloat(context, "mix");
                OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                orchestrator.setOutlineColor(r, g, b, mix);
                context.getSource().sendFeedback(Component.literal("[EO] color set to: (" + r + ", " + g + ", " + b + "), mix=" + mix));
                return 1;
            });

            colorB.then(colorMix);
            colorG.then(colorB);
            colorR.then(colorG);
            colorNode.then(colorR);
            root.then(colorNode);

            root.then(ClientCommandManager.literal("glow")
                    .then(ClientCommandManager.argument("strength", FloatArgumentType.floatArg(0.0F, 4.0F))
                            .executes(context -> {
                                float glow = FloatArgumentType.getFloat(context, "strength");
                                OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                                orchestrator.setOutlineGlow(glow);
                                float activeGlow = orchestrator.currentSettings().outlineGlow();
                                context.getSource().sendFeedback(Component.literal("[EO] glow set to: " + activeGlow));
                                return 1;
                            })));

            root.then(ClientCommandManager.literal("status")
                    .executes(context -> {
                        OutlineRenderOrchestrator orchestrator = OutlineRenderOrchestrator.getInstance();
                        String mode = OutlineTechniqueManager.getInstance().getCurrentMode().id();
                        int radius = orchestrator.currentSettings().outlineRadiusPixels();
                        float glow = orchestrator.currentSettings().outlineGlow();
                        float[] rgb = orchestrator.currentSettings().outlineColorRgb();
                        float mix = orchestrator.currentSettings().outlineColorMix();
                        context.getSource().sendFeedback(Component.literal("[EO] mode=" + mode
                                + ", radius=" + radius
                                + ", glow=" + glow
                                + ", color=[" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "]"
                                + ", mix=" + mix));
                        return 1;
                    }));

            dispatcher.register(root);
        });
    }
}




