/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.cutscene.dialogue.DialogueContext
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueScene
import com.almasb.fxgl.cutscene.dialogue.FunctionCallHandler
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService
import javafx.scene.media.MediaView

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneService : EngineService() {

    private lateinit var assetLoader: AssetLoaderService
    private lateinit var sceneService: SceneService
    private lateinit var audioPlayer: AudioPlayer

    private var gameVars: PropertyMap? = null

    private val scene by lazy { CutsceneScene(sceneService) }
    private val dialogueScene by lazy { DialogueScene(sceneService) }
    private val videoScene by lazy { VideoScene(sceneService) }

    @JvmOverloads fun startCutscene(cutscene: Cutscene, onFinished: Runnable = EmptyRunnable) {
        scene.assetLoader = assetLoader
        scene.start(cutscene, onFinished)
    }

    @JvmOverloads fun startDialogueScene(
            dialogueGraph: DialogueGraph,
            context: DialogueContext = TemporaryContext(),
            functionHandler: FunctionCallHandler = EmptyFunctionCallHandler,
            onFinished: Runnable = EmptyRunnable) {

        dialogueScene.gameVars = gameVars ?: throw IllegalStateException("Cannot start dialogue scene. The game is not initialized yet.")
        dialogueScene.assetLoader = assetLoader
        dialogueScene.audioPlayer = audioPlayer
        dialogueScene.start(dialogueGraph, context, functionHandler, onFinished)
    }

    @JvmOverloads fun startVideoCutscene(video: MediaView, onFinished: Runnable = EmptyRunnable) {
        videoScene.start(video, onFinished)
    }

    override fun onGameReady(vars: PropertyMap) {
        gameVars = vars
    }
}

private class TemporaryContext : DialogueContext {
    override fun properties(): PropertyMap = PropertyMap()
}

private object EmptyFunctionCallHandler : FunctionCallHandler()