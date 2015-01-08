package com.rts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import android.graphics.Typeface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.andengine.input.sensor.orientation.IOrientationListener;
import org.andengine.input.sensor.orientation.OrientationData;
import org.andengine.input.sensor.orientation.OrientationSensorOptions;
import org.andengine.input.sensor.SensorDelay;
import org.andengine.input.touch.TouchEvent;
import android.view.KeyEvent;
import java.text.DecimalFormat;
import android.view.MotionEvent;
import com.rts.RtsHUD;
import com.rts.PlayerView;
import com.rts.PlayerModel;

public class MainActivity extends BaseGameActivity implements IOrientationListener
{
	private SensorManager sm;
	private SmoothCamera mCamera;
	public static final int CAMERA_WIDTH = 480;
	public static final int CAMERA_HEIGHT = 320;
	public static final int BG_WIDTH = 2544;
	public static final int BG_HEIGHT = 2458;
	public static final float CAMERA_X_OFFSET = 230f;
	public static final float CAMERA_Y_OFFSET = 160f;
	public static final float CAMERA_VX = 200f;
	public static final float CAMERA_VY = 200f;
	public static final float CAMERA_ZOOM = 1;
	public static float TILT_ORIGIN = 0f; //-20f
	public static float TILT_SENSITIVITY = 15f;
	public static float SCROLL_SPEED = 20f;
	
	public boolean showSensor;
	public float cameraX=CAMERA_X_OFFSET;
	public float cameraY=BG_HEIGHT-CAMERA_HEIGHT+CAMERA_Y_OFFSET;
	float[] rotationMatrix = new float[16];
	float[] magField = new float[3];
	float[] accel = new float[3];
	float[] orientation = new float[3];
	public static Font mFont;
	public DecimalFormat mFormat;
	Text[] vals;
	private ITextureRegion splashTexture;
	private ITextureRegion bgTexture;
	private ITextureRegion buttonTexture;
	private ITextureRegion[] hudTextures;
	public TiledTextureRegion playerTexture;
	public TiledTextureRegion moveArrowTexture;
	public Scene scene;
	public RtsHUD hud; 
	public PlayerView pv;
	public PlayerModel pm;
	//public AnimatedSprite playerSprite;

    public EngineOptions onCreateEngineOptions() {
		setPreferences();
		mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
						CAMERA_VX,CAMERA_VY,CAMERA_ZOOM);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}
	
	public void onOrientationAccuracyChanged(final OrientationData pOrientationData){};
	public void onOrientationChanged(final OrientationData pOrientationData){
		if (vals!=null) {
			vals[0].setText("yaw: "+mFormat.format(pOrientationData.getYaw()));
			vals[1].setText("pitch: "+mFormat.format(pOrientationData.getPitch()));
			vals[2].setText("roll: "+mFormat.format(pOrientationData.getRoll()));
		}	
		if (pOrientationData.getPitch()-TILT_ORIGIN>TILT_SENSITIVITY) scroll(3);
		else if (pOrientationData.getPitch()-TILT_ORIGIN<-TILT_SENSITIVITY) scroll(2);
		else if (pOrientationData.getRoll()>TILT_SENSITIVITY) scroll(0);
		else if (pOrientationData.getRoll()<-TILT_SENSITIVITY) scroll(1);
	}
	
	@Override
	public void onResumeGame() {
		setPreferences();
		super.onResumeGame();
	}
	
	public void setPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ts = sharedPreferences.getString("tilt_sensitivity", "Medium");
		String ss = sharedPreferences.getString("scroll_speed", "Medium");
		String nt = sharedPreferences.getString("natural_tilt", "Horizontal");
		showSensor = sharedPreferences.getBoolean("show_sensor", true);
		if (ts.equals("High")) TILT_SENSITIVITY=10f;
		else if (ts.equals("Low")) TILT_SENSITIVITY=25f;
		else TILT_SENSITIVITY=15f;
		if (ss.equals("Fast")) SCROLL_SPEED=30f;
		else if (ss.equals("Slow")) SCROLL_SPEED=10f;
		else SCROLL_SPEED=20f;
		if (nt.equals("Vertical")) TILT_ORIGIN=-90f;
		else if (nt.equals("45 degress")) TILT_ORIGIN=-45f;
		else if (nt.equals("30 degress")) TILT_ORIGIN=-30f;
		else if (nt.equals("15 degress")) TILT_ORIGIN=-15f;
		else TILT_ORIGIN=0f;
		System.out.println("tilt sensitivity is "+ts);
		System.out.println("scroll speed is "+ss);
		System.out.println("natural tilt is "+nt);
		System.out.println("show sensor is "+showSensor);
	}
	
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else return super.onKeyDown(pKeyCode, pEvent);

	}
	
	public final void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas splashAtlas = new BitmapTextureAtlas(getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		splashTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashAtlas, this, "splashscreen.jpg", 0, 0);
		splashAtlas.load();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public final void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		Scene mSplashScene = new Scene();
		mSplashScene.setBackgroundEnabled(false);
		mSplashScene.attachChild(new Sprite(0, 0, splashTexture, this.getVertexBufferObjectManager()));
		pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
	}
	
	public final void onPopulateScene(final Scene pScene, final OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		final MainActivity ma = this;
		mEngine.registerUpdateHandler(new TimerHandler(0.01f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				loadResources();loadScene();
				try{Thread.sleep(3000);}
				catch (InterruptedException e){e.printStackTrace();}
				//mCamera.setZoomFactorDirect(2.5f);
				mCamera.setCenterDirect(cameraX,cameraY);
				mEngine.setScene(scene);
				//ma.enableOrientationSensor(ma);
			}
        }));		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas bgAtlas = new BitmapTextureAtlas(getTextureManager(), 2048, 2048, TextureOptions.NEAREST);
		bgTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bgAtlas, this, "map.jpg", 0, 0);
		bgAtlas.load();
		hudTextures = new ITextureRegion[4];
		BitmapTextureAtlas minimapAtlas = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		hudTextures[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(minimapAtlas, this, "minimap.jpg", 0, 0);
		minimapAtlas.load();
		BitmapTextureAtlas buttonAtlas = new BitmapTextureAtlas(getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		buttonTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buttonAtlas, this, "button.png", 0, 0);
		buttonAtlas.load();
		BitmapTextureAtlas playerAtlas = new BitmapTextureAtlas(getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		playerTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png",0,0,3, 4);
		playerAtlas.load();
		BitmapTextureAtlas moveArrowAtlas = new BitmapTextureAtlas(getTextureManager(), 512, 64, TextureOptions.BILINEAR);
		moveArrowTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(moveArrowAtlas, this, "move-arrow.png",0,0,4,1);
		moveArrowAtlas.load();
		BitmapTextureAtlas actionAtlas = new BitmapTextureAtlas(getTextureManager(), 128, 1024, TextureOptions.BILINEAR);
		hudTextures[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(actionAtlas, this, "move.png",0,0);
		hudTextures[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(actionAtlas, this, "attack.png",0,128);
		hudTextures[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(actionAtlas, this, "stop.png",0,256);
		actionAtlas.load();
	}

	public void loadScene() {
		scene=new Scene();
		Sprite bgSprite = new Sprite(0, 0,BG_WIDTH,BG_HEIGHT,bgTexture, getVertexBufferObjectManager());
		scene.attachChild(bgSprite);
			
		mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 12,true,Color.WHITE);
		mFont.load();
		
		mFormat=new DecimalFormat("#####");
		
		hud = new RtsHUD(this,mCamera,getVertexBufferObjectManager(),hudTextures);
		hud.maxHP=200;
		hud.maxMana=50;
		hud.setHP(0.5f);
		hud.setMana(0.5f);
		hud.updateMinimap(cameraX-CAMERA_X_OFFSET,cameraY-CAMERA_Y_OFFSET);
		mCamera.setHUD(hud);
		//for debugging: scroll buttons
		/*Sprite[] buttons = new Sprite[4];
		for (int i=0;i<4;i++) {
			final int index=i;
			buttons[i]=new Sprite(400,50+i*50,buttonTexture,getVertexBufferObjectManager()) {
				public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				   if(pTouchEvent.isActionDown()) {
						scroll(index);
				   }
				   return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			   }
			};
			hud.registerTouchArea(buttons[i]);
			hud.addEntity(buttons[i]);
		}*/
		
		//for debugging: text box to display sensor data
		if (showSensor) {
			vals = new Text[4];
			vals[0] = new Text(150, 100,mFont, "yaw: 0.00000000",getVertexBufferObjectManager());
			vals[1] = new Text(150, 120,mFont, "pitch: 0.00000000",getVertexBufferObjectManager());
			vals[2] = new Text(150, 140,mFont, "roll: 0.00000000",getVertexBufferObjectManager());
			vals[3] = new Text(150, 160,mFont, "no motion  ",getVertexBufferObjectManager());
			hud.addEntity(vals[0]);
			hud.addEntity(vals[1]);
			hud.addEntity(vals[2]);
			hud.addEntity(vals[3]);
		}
		
		pm = new PlayerModel(200,50,100);
		pv = new PlayerView(pm,scene,hud,playerTexture,getVertexBufferObjectManager());
		
		enableOrientationSensor(this, new OrientationSensorOptions(SensorDelay.UI));
	}

	public void scroll(int direction) {
		String motion="";
		switch (direction) {
			case 0:cameraX+=SCROLL_SPEED; motion="right"; break;
			case 1:cameraX-=SCROLL_SPEED; motion="left";break;
			case 2:cameraY+=SCROLL_SPEED; motion="down";break;
			case 3:cameraY-=SCROLL_SPEED; motion="up";break;
		}
		System.out.println(motion);
		vals[3].setText(motion);
		if (cameraX<CAMERA_X_OFFSET) cameraX=CAMERA_X_OFFSET;
		else if (cameraX>(BG_WIDTH-CAMERA_WIDTH+CAMERA_X_OFFSET)) cameraX=(BG_WIDTH-CAMERA_WIDTH+CAMERA_X_OFFSET);
		if (cameraY<CAMERA_Y_OFFSET) cameraY=CAMERA_Y_OFFSET;
		else if (cameraY>(BG_HEIGHT-CAMERA_HEIGHT+CAMERA_Y_OFFSET)) cameraY=(BG_HEIGHT-CAMERA_HEIGHT+CAMERA_Y_OFFSET);
		mCamera.setCenter(cameraX,cameraY);
		hud.updateMinimap(cameraX-CAMERA_X_OFFSET,cameraY-CAMERA_Y_OFFSET);
	}
	
	public void moveMinimap(float onMinimapX, float onMinimapY) {
		cameraX=(onMinimapX-RtsHUD.minimapWidth/2)/RtsHUD.minimapSizeX*BG_WIDTH+CAMERA_X_OFFSET;
		cameraY=(onMinimapY-RtsHUD.minimapHeight/2)/RtsHUD.minimapSizeY*BG_HEIGHT+CAMERA_Y_OFFSET;
		if (cameraX<CAMERA_X_OFFSET) cameraX=CAMERA_X_OFFSET;
		else if (cameraX>(BG_WIDTH-CAMERA_WIDTH+CAMERA_X_OFFSET)) cameraX=(BG_WIDTH-CAMERA_WIDTH+CAMERA_X_OFFSET);
		if (cameraY<CAMERA_Y_OFFSET) cameraY=CAMERA_Y_OFFSET;
		else if (cameraY>(BG_HEIGHT-CAMERA_HEIGHT+CAMERA_Y_OFFSET)) cameraY=(BG_HEIGHT-CAMERA_HEIGHT+CAMERA_Y_OFFSET);
		mCamera.setCenterDirect(cameraX,cameraY);
		hud.updateMinimap(cameraX-CAMERA_X_OFFSET,cameraY-CAMERA_Y_OFFSET);
	}
}
