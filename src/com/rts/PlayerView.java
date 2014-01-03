package com.rts;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.IEntity;
import org.andengine.util.modifier.IModifier;
import org.andengine.entity.primitive.Rectangle;
import com.rts.PlayerModel;
import com.rts.RtsHUD;

public class PlayerView {

	public static float minimapMarkerSize=5f;
	
	public float spawnX=240f;
	public float spawnY=MainActivity.BG_HEIGHT-200f;
	public float spriteWidth=48f;
	public float spriteHeight=64f;
	public AnimatedSprite playerSprite;
	public Rectangle minimapMarker;
	public PlayerModel pm;
	

	public PlayerView(PlayerModel pm, Scene scene, RtsHUD hud, TiledTextureRegion playerTexture, VertexBufferObjectManager vbo) {
		this.pm=pm;
		playerSprite = new AnimatedSprite(spawnX,spawnY,spriteWidth,spriteHeight, playerTexture,vbo);
		//playerSprite.animate(new long[]{200, 200, 200}, 6, 8, true);
		scene.attachChild(playerSprite);
		
		minimapMarker=new Rectangle(spawnX*RtsHUD.minimapScaleXFactor,spawnY*RtsHUD.minimapScaleYFactor+RtsHUD.minimapY,minimapMarkerSize,minimapMarkerSize,vbo);
		minimapMarker.setColor(0f,1f,0f,1f);
		hud.addEntity(minimapMarker);
	}

	public void move(float x, float y) {
		stop();
		float finalX=(x-spriteWidth/2);
		float finalY=(y-spriteHeight/2);
		float dx = finalX-playerSprite.getX();
		float dy = finalY-playerSprite.getY();
		if (Math.abs(dx)<0.001&&Math.abs(dy)<0.001) return;
		double duration = Math.sqrt(dx*dx+dy*dy)/pm.moveSpeed;
		MoveModifier spriteModifier = new MoveModifier((float)duration,playerSprite.getX(),finalX,playerSprite.getY(),finalY,new IEntityModifier.IEntityModifierListener() {
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem){};
			public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem){
				playerSprite.stopAnimation();
			};
		});
		playerSprite.registerEntityModifier(spriteModifier);
		MoveModifier markerModifier = new MoveModifier((float)duration,minimapMarker.getX(),minimapMarker.getX()+dx*RtsHUD.minimapScaleXFactor,
											minimapMarker.getY(),minimapMarker.getY()+dy*RtsHUD.minimapScaleYFactor);
		minimapMarker.registerEntityModifier(markerModifier);
		if (Math.abs(dy)>Math.abs(dx)) {
			if (dy>0) playerSprite.animate(new long[] {200,200,200},6,8,true);
			else playerSprite.animate(new long[] {200,200,200},0,2,true);
		} else {
			if (dx>0) playerSprite.animate(new long[] {200,200,200},3,5,true);
			else playerSprite.animate(new long[] {200,200,200},9,11,true);
		}
	}
	
	public void stop() {
		playerSprite.clearEntityModifiers();
		minimapMarker.clearEntityModifiers();
		playerSprite.stopAnimation();
	}
	
}