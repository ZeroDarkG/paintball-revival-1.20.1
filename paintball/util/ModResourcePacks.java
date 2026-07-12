package com.zerokg2004.paintball.util; // Ajusta a tu paquete util o events

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModResourcePacks {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MODID = "paintball"; // Cambia esto si tu ID es otro

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        // Registro del Pack "Alternative Chestplate"
        registerPack(
                event,
                "alternative_chestplate",
                "alternative_chestplate", // ID interno del pack
                "Alternative Chestplate"  // Título que ve el jugador
        );
    }

    private static void registerPack(
            AddPackFindersEvent event,
            String folderName,
            String packId,
            String title
    ) {
        Path packPath = ModList.get()
                .getModFileById(MODID)
                .getFile()
                .findResource("resourcepacks/" + folderName);

        // Verificamos si la carpeta existe para evitar errores
        if (!Files.exists(packPath)) {
            LOGGER.error("[{}] Carpeta de Pack no encontrada: {}", MODID, packPath);
            return;
        }

        // En 1.20.1 usamos Pack.readMetaAndCreate con un nombre de componente literal
        Pack pack = Pack.readMetaAndCreate(
                MODID + ":" + packId,
                Component.literal(title),
                false, // ¿Es obligatorio? false permite desactivarlo
                (id) -> new PathPackResources(id, packPath, false),
                PackType.CLIENT_RESOURCES,
                Pack.Position.TOP,
                PackSource.BUILT_IN
        );

        if (pack != null) {
            event.addRepositorySource((consumer) -> consumer.accept(pack));
            LOGGER.info("[{}] Pack integrado registrado con éxito: {}", MODID, title);
        }
    }
}