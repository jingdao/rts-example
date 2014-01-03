package com.rts;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import com.rts.MainActivity;

public class RtsHUD extends HUD {

	public static float minimapSizeX=100f;
	public static float minimapSizeY=97f;
	public static float hpManaX=150f;
	public static float minimapY=220f;
	public static float hpY=220f;
	public static float manaY=240f;
	public static float actionY=260f;
	public static float actionSize=50f;
	public static float rectWidth=200f;
	public static float rectHeight=15f;
	public static float lineWidth=2f;
	public static float minimapWidth=(float)(1.0*MainActivity.CAMERA_WIDTH/MainActivity.BG_WIDTH*minimapSizeX);
	public static float minimapHeight=(float)(1.0*MainActivity.CAMERA_HEIGHT/MainActivity.BG_HEIGHT*minimapSizeY);
	public static float minimapScaleXFactor = minimapSizeX/MainActivity.BG_WIDTH;
	public static float minimapScaleYFactor = minimapSizeY/MainActivity.BG_HEIGHT;
	public static float moveArrowWidth=50f;
	public static float moveArrowHeight=50f;
	
	public static int maxHP=100;
	public static int maxMana=100;
	
	public MainActivity ma;
	public VertexBufferObjectManager vbo;
	public Rectangle[] progress;
	public Text[] statusText;
	public Line[] miniFrame;
	public Rectangle touchArea;
	public Sprite minimap;
	public Sprite[] actionButtons;
	public AnimatedSprite moveArrowSprite=null;

	public RtsHUD(MainActivity main, final Camera pCamera,final VertexBufferObjectManager vbo,ITextureRegion[] hudTextures) {
		super();
		this.ma=main;
		this.vbo=vbo;
		super.setCamera(pCamera);
		
		minimap = new Sprite(0,minimapY,hudTextures[0],vbo);
		registerTouchArea(minimap);
		super.attachChild(minimap);
		
		Rectangle background[] = new Rectangle[2];
		progress = new Rectangle[2];
		Line frame[] = new Line[8];
		miniFrame = new Line[4];
		
		background[0]=new Rectangle(hpManaX,hpY,rectWidth,rectHeight,vbo);
		background[1]=new Rectangle(hpManaX,manaY,rectWidth,rectHeight,vbo);
		progress[0]=new Rectangle(hpManaX,hpY,rectWidth,rectHeight,vbo);
		progress[1]=new Rectangle(hpManaX,manaY,rectWidth,rectHeight,vbo);
		frame[0] = new Line(hpManaX,hpY,hpManaX+rectWidth,hpY,lineWidth,vbo); //Top line.
		frame[1] = new Line(hpManaX+rectWidth,hpY,hpManaX+rectWidth,hpY+rectHeight,lineWidth,vbo); //Right line.
		frame[2] = new Line(hpManaX,hpY+rectHeight,hpManaX+rectWidth,hpY+rectHeight,lineWidth,vbo); //Bottom line.
		frame[3] = new Line(hpManaX,hpY,hpManaX,hpY+rectHeight,lineWidth,vbo); //Left line.
		frame[4] = new Line(hpManaX,manaY,hpManaX+rectWidth,manaY,lineWidth,vbo); //Top line.
		frame[5] = new Line(hpManaX+rectWidth,manaY,hpManaX+rectWidth,manaY+rectHeight,lineWidth,vbo); //Right line.
		frame[6] = new Line(hpManaX,manaY+rectHeight,hpManaX+rectWidth,manaY+rectHeight,lineWidth,vbo); //Bottom line.
		frame[7] = new Line(hpManaX,manaY,hpManaX,manaY+rectHeight,lineWidth,vbo); //Left line.
		miniFrame[0] = new Line(0,minimapY,minimapWidth,minimapY,lineWidth,vbo); //Top line.
		miniFrame[1] = new Line(minimapWidth,minimapY,minimapWidth,minimapY+minimapHeight,lineWidth,vbo); //Right line.
		miniFrame[2] = new Line(0,minimapY+minimapHeight,minimapWidth,minimapY+minimapHeight,lineWidth,vbo); //Bottom line.
		miniFrame[3] = new Line(0,minimapY,0,minimapY+minimapHeight,lineWidth,vbo); //Left line.
		
		background[0].setColor(0f,0f,0f,1f);
		background[1].setColor(0f,0f,0f,1f);
		progress[0].setColor(0f,1f,0f,1f);
		progress[1].setColor(0f,0f,1f,1f);
		for (Rectangle r: background) attachChild(r);
		for (Rectangle r: progress) attachChild(r);
		for (Line l:frame) {
			l.setColor(0.2f,0.2f,0.2f,1f); attachChild(l);
		}	
		for (Line l:miniFrame) {
			l.setColor(1f,1f,1f,1f); attachChild(l);
		}	
		
		statusText = new Text[2];
		statusText[0] = new Text((float)(hpManaX+rectWidth*0.35), hpY,MainActivity.mFont,maxHP+" / "+maxHP,vbo);
		statusText[1] = new Text((float)(hpManaX+rectWidth*0.35), manaY,MainActivity.mFont,maxMana+" / "+maxMana,vbo);
		attachChild(statusText[0]);attachChild(statusText[1]);
		
		actionButtons = new Sprite[3];
		for (int i=0;i<3;i++) {
			actionButtons[i] = new Sprite(hpManaX+(actionSize+5)*i,actionY,actionSize,actionSize,hudTextures[i+1],vbo);
			registerTouchArea(actionButtons[i]);
			attachChild(actionButtons[i]);
		}
		
		
		/*touchArea = new Rectangle(0,0,MainActivity.CAMERA_WIDTH,MainActivity.CAMERA_HEIGHT,vbo) {
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			   if(pTouchEvent.isActionDown()) {
					System.out.println("x: "+pTouchAreaLocalX+" y: "+pTouchAreaLocalY);
			   }
			   return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		   }
		};
		touchArea.setColor(0f,0f,0f,0f);
		registerTouchArea(touchArea);
		attachChild(touchArea);*/
		touchArea = new Rectangle(0,0,MainActivity.CAMERA_WIDTH,minimapY,vbo);
		touchArea.setColor(0f,0f,0f,0f);
		registerTouchArea(touchArea);
		attachChild(touchArea);
		setOnAreaTouchListener(new IOnAreaTouchListener() {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					if (pTouchArea.equals(minimap)) {
						System.out.println("minimap touched "+pTouchAreaLocalX+" "+pTouchAreaLocalY);
						ma.moveMinimap(pTouchAreaLocalX,pTouchAreaLocalY);
					}else if (pTouchArea.equals(touchArea)) {
						System.out.println("toucharea touched "+pTouchAreaLocalX+" "+pTouchAreaLocalY);
						setMoveArrow(pTouchAreaLocalX,pTouchAreaLocalY);
						ma.pv.move(pTouchAreaLocalX+ma.cameraX-MainActivity.CAMERA_X_OFFSET,pTouchAreaLocalY+ma.cameraY-MainActivity.CAMERA_Y_OFFSET);
					}
					else if (pTouchArea.equals(actionButtons[0])) System.out.println("action button 0");
					else if (pTouchArea.equals(actionButtons[1])) {
						System.out.println("action button 1");
					}
					else if (pTouchArea.equals(actionButtons[2])) {
						System.out.println("action button 2");
						ma.pv.stop();
					}
					else System.out.println("x: "+pTouchAreaLocalX+" y: "+pTouchAreaLocalY);
				}
				return false;
			}
		});
		
	}
	
	public void setHP(float n) {
		if (n<0) progress[0].setWidth(0);
		if (n>1) progress[0].setWidth(rectWidth);
		progress[0].setWidth(rectWidth*n);
		statusText[0].setText((int)(n*maxHP)+" / "+(int)maxHP);
	}
	
	public void setMana(float n) {
		if (n<0) progress[1].setWidth(0);
		if (n>1) progress[1].setWidth(rectWidth);
		progress[1].setWidth(rectWidth*n);
		statusText[1].setText((int)(n*maxMana)+" / "+(int)maxMana);
	}
	
	public void addEntity(Entity s) {
		attachChild(s);
	}
	
	public void updateMinimap(float cameraX,float cameraY) {
		float cameraXX = (float)(1.0*cameraX/MainActivity.BG_WIDTH*minimapSizeX+lineWidth);
		float cameraYY = (float)(1.0*cameraY/MainActivity.BG_HEIGHT*minimapSizeY);
		miniFrame[0].setPosition(cameraXX,minimapY+cameraYY,cameraXX+minimapWidth,minimapY+cameraYY); //Top line.
		miniFrame[1].setPosition(cameraXX+minimapWidth,minimapY+cameraYY,cameraXX+minimapWidth,minimapY+cameraYY+minimapHeight); //Right line.
		miniFrame[2].setPosition(cameraXX,minimapY+cameraYY+minimapHeight,cameraXX+minimapWidth,minimapY+cameraYY+minimapHeight); //Bottom line.
		miniFrame[3].setPosition(cameraXX,minimapY+cameraYY,cameraXX,minimapY+cameraYY+minimapHeight); //Left line.
	}
	
	public void setMoveArrow(float x, float y) {
		if (moveArrowSprite!=null) {
			moveArrowSprite.stopAnimation();
			detachChild(moveArrowSprite);
		}
		moveArrowSprite = new AnimatedSprite(x-moveArrowWidth/2,y-moveArrowHeight/2,ma.moveArrowTexture,vbo);
		moveArrowSprite.animate(100,false,new AnimatedSprite.IAnimationListener() {
			public void onAnimationStarted(final AnimatedSprite pAnimatedSprite, final int pInitialLoopCount){};
			public void onAnimationFrameChanged(final AnimatedSprite pAnimatedSprite, final int pOldFrameIndex, final int pNewFrameIndex){};
			public void onAnimationLoopFinished(final AnimatedSprite pAnimatedSprite, final int pRemainingLoopCount, final int pInitialLoopCount){};
			public void onAnimationFinished(final AnimatedSprite pAnimatedSprite){
				detachChild(moveArrowSprite);
				moveArrowSprite=null;
			};
		});
		attachChild(moveArrowSprite);
	}
}