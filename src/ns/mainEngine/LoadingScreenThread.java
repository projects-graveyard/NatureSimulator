package ns.mainEngine;

import data.GameData;
import ns.components.BlueprintCreator;
import ns.customFileFormat.TexFile;
import ns.entities.Entity;
import ns.fontMeshCreator.FontType;
import ns.fontMeshCreator.GUIText;
import ns.fontRendering.TextMaster;
import ns.parallelComputing.GLClearRequest;
import ns.parallelComputing.GLRenderRequest;
import ns.parallelComputing.GLRenderRequest.RenderMethod;
import ns.parallelComputing.UpdateDisplayRequest;
import ns.renderers.GUIRenderer;
import ns.ui.loadingScreen.InGameLogo;
import ns.utils.GU;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

class LoadingScreenThread implements Runnable {
	private static final float SPD = 0.01f;
	private static final int logo_index = 3;

	private float alphaCoef = 0.1f;
	private int textI = 0;
	static boolean READY = false;
	private GUIText text;
	private boolean incr = true;

	@Override
	public void run() {
		GU.currentThread().waitForGameDataInit();
		FontType z003 = new FontType(new TexFile("fonts/Z003.tex").load(),
				GU.open(GameData.getResourceAt("fonts/Z003.fnt")));
		GU.setZ003(z003);
		FontType caladea = new FontType(new TexFile("fonts/Caladea.tex").load(),
				GU.open(GameData.getResourceAt("fonts/Caladea.fnt")));
		GU.setCaladea(caladea);
		List<GUIText> textToShow = new ArrayList<>();
		textToShow.add(new GUIText("Made by", 5f, z003, new Vector2f(0.0f, 0.0f), 0.4f, true));
		textToShow.add(new GUIText("NonNullDinu", 3f, caladea, new Vector2f(0.0f, 0.0f), 0.4f, true));
		textToShow.add(new GUIText("And Mahou-sama666", 3f, caladea, new Vector2f(0.0f, 0.0f), 0.4f, true));
		textToShow.add(new GUIText("Loading...", 2f, z003, new Vector2f(0.0f, 0.0f), 0.2f, true));
		InGameLogo logo = new InGameLogo(new Entity(BlueprintCreator.createModelBlueprintFor("logo"), new Vector3f(0,
				0, -8f
		)));
		text = textToShow.get(textI);
		TextMaster.loadText(text);
		text.setColour(0f, 0f, 0f);
		TextMaster.add(text);
		RenderMethod renderMethod = () -> {
			if (!READY) {
				addToAC(incr ? SPD : -SPD);
				if (alphaCoef >= 1.0)
					setIncr(false);
			} else {
				addToAC(1.0f - alphaCoef);
			}
			if (alphaCoef <= 0.0f && textI != logo_index) {
				if (READY) {
					setText(textToShow.get(textI));
					TextMaster.add(text);
					TextMaster.render(alphaCoef);
				} else {
					addToAC(-alphaCoef);
					if (textI != logo_index)
						text.remove();
					textI++;
					setReady(textI >= textToShow.size());
					setBreak(READY);
					setIncr(true);
					setText(textToShow.get(textI > logo_index ? textI - 1 : textI));
					TextMaster.add(text);
				}
			}
			if (textI == logo_index) {
				logo.render(GUIRenderer.instance, null);
				if (logo.done()) {
					textI++;
					setReady(textI >= textToShow.size());
					setBreak(READY);
					setIncr(true);
					setText(textToShow.get(textI - 1));
					TextMaster.add(text);
				}
			} else
				TextMaster.render(alphaCoef);
		};
		GU.currentThread().finishLoading();
		while (MainGameLoop.state == GS.LOADING || !READY) {
			GU.sendRequestToMainThread(
					new GLClearRequest(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, new Vector3f(1, 1, 1)));
			GU.sendRequestToMainThread(new GLRenderRequest(renderMethod));
			GU.sendRequestToMainThread(new UpdateDisplayRequest());
			for (int i = textI + 1; i < textToShow.size(); i++)
				TextMaster.loadTextIfNotLoadedAlready(textToShow.get(i));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TextMaster.remove(text);
		GU.currentThread().finishExecution();
	}

	private void setBreak(boolean b) {
		boolean toBreak = b;
	}

	private void setIncr(boolean b) {
		incr = b;
	}

	private void addToAC(float d) {
		alphaCoef += d;
	}

	private void setText(GUIText text) {
		this.text = text;
	}

	private void setReady(boolean b) {
		READY = b;
	}
}