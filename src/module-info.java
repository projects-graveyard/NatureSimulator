/**
 * The game module
 * 
 * @author Dinu B.
 */
module GameModule {
	// Dependencies
	requires java.desktop;
	requires lwjgl;
	requires lwjgl.util;

	// src
	exports ns.mainEngine;

	// audioEngine
	exports ns.openALObjects;

	// resources
	exports obj;
	exports res;

	// gameEngine
	exports ns.camera;
	exports ns.components;
	exports ns.configuration;
	exports ns.customFileFormat;
	exports ns.entities;
	exports ns.exceptions;
	exports ns.mainMenu;
	exports ns.parallelComputing;
	exports ns.terrain;
	exports ns.ui;
	exports ns.utils;
	exports ns.water;
	exports ns.world;
	exports ns.worldSave;

	// renderEngine
	exports ns.display;
	exports ns.openglObjects;
	exports ns.openglWorkers;
	exports ns.renderers;
	exports ns.shaders;
	exports ns.shaders.uniformStructs;

	// Shaders
	opens res.shaders.standard;
	opens res.shaders.terrain;
	opens res.shaders.water;
	opens res.shaders.depthFieldBlur;
	opens res.shaders.blur;
	opens res.shaders.guis;
	opens res.shaders.menuDNA;
	opens res.shaders.quad;

	// Textures
	opens res.textures;
	opens res.textures.buttonTextures;
	opens res.textures.lensFlare;

	// Models
	opens res.models.others;
}