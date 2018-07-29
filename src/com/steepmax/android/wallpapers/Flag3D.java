package com.steepmax.android.wallpapers;

import java.nio.FloatBuffer;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.BufferUtils;
import com.eightbitmage.gdxlw.LibdgxWallpaperListener;


public class Flag3D  implements ApplicationListener, InputProcessor, LibdgxWallpaperListener{
	
	public final static String[] texts = { "Skiing Destination",
										  "High Tatras",
										  "Rich Heritage",
										  "Major Trading Power"};
	
	private TextItem items[] = new TextItem[texts.length];
	
	ImmediateModeRenderer10 renderer;
	TerrainChunk chunk;
	Mesh mesh;
	PerspectiveCamera camera;
	Vector3 intersection = new Vector3();
	boolean intersected = false;
	long lastTime = System.nanoTime();

	double delta = 0;
	private Texture texture;
	private SpriteBatch batch;
	private BitmapFont font1;
	private BitmapFont font2;

	private int height;

	private int width;
	
	public void create () {
		renderer = new ImmediateModeRenderer10();

		chunk = new TerrainChunk(12, 12, 6);

		
	
		
		Random rand = new Random();
		int len = chunk.vertices.length;
		for (int i = 3; i < len; i += 6) {
			
		    int val = 255;
		//	int val = (int) (Math.sin((Math.PI/12)*(i%12))*100+155);
			
			chunk.vertices[i] = Color.toFloatBits(val,val,val,val);
			
		}

		mesh = new Mesh(true, 
								chunk.vertices.length / 6, 
								chunk.indices.length, 
								new VertexAttribute(VertexAttributes.Usage.Position,3, "a_position"), 
								new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"),
								new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords")
				);

		
		
		mesh.setVertices(chunk.vertices);
		mesh.setIndices(chunk.indices);

		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	//	camera.position.set(0, 5, 5);
		camera.position.set(1,5,5);
		camera.direction.set(6, 0, 6).sub(camera.position).nor();
		camera.near = 0.5f;
		camera.far = 300;
		
		FileHandle imageFileHandle = Gdx.files.internal("flag3.png"); 
	    texture = new Texture(imageFileHandle);
	    
	    batch = new SpriteBatch();
	    
	    font1 = new BitmapFont(Gdx.files.internal("font16.fnt"),
 				Gdx.files.internal("font16.png"), false);
	}

	public void render () {
		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		//---------------
		
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
	
		batch.begin();
	
		
		float fac = (float)width/252f;
		
		batch.draw(texture, 0, height-173f*fac,  //screen coords
				0, 0,   //center of rotation
				252 , 173,  //image size 
				fac,fac, //scaleX factor, scaleY factor
				0, //rotation in degrees [0..360]
				260,0, //texture window offset x,y pixels
				252, 173,  //texture window size w,h pixels
				false, false);  //flip vert. horiz.
		
		
		batch.draw(texture, width/2f, height,  //screen coords
				252*fac/2f, (512f-173f)*fac,   //center of rotation
				252 , (512f-173f),  //image size 
				fac,fac, //scaleX factor, scaleY factor
				(float)(Math.sin(delta/6)*20d), //rotation in degrees [0..360]
				260,175, //texture window offset x,y pixels
				512-260, 512-173,  //texture window size w,h pixels
				false, false);  //flip vert. horiz.
		
		
		batch.draw(texture, width/2f, height,  //screen coords
				252*fac/2f, (512f-173f)*fac,   //center of rotation
				252 , (512f-173f),  //image size 
				fac,fac, //scaleX factor, scaleY factor
				(float)(Math.cos(delta/6)*20d), //rotation in degrees [0..360]
				260,175, //texture window offset x,y pixels
				512-260, 512-173,  //texture window size w,h pixels
				false, false);  //flip vert. horiz.
	
		
		
		
		batch.end();
		
		
		
		
		
		//---------------
	
		
		delta += 0.1;
		
		camera.update();
		camera.apply(gl);
		gl.glColor4f(1, 1, 1, 1);
		
		chunk.animateVertices(delta);
		mesh.setVertices(chunk.vertices);		

		
	
		
	    Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
		texture.bind();
	
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	
		mesh.render(GL10.GL_TRIANGLES);
		
		
		
		
		

		if (intersected) {
			gl.glPointSize(10);
			renderer.begin(GL10.GL_POINTS);
			renderer.color(1, 0, 0, 1);
			renderer.vertex(intersection.x, intersection.y, intersection.z);
			renderer.end();
		}
		
		
		batch.begin();
		
		for (int i = 0;  i < texts.length; i++) {
			items[i].compute();
			
			font1.setScale(items[i].getSize());
			font1.setColor(1,1,1,items[i].getAlpha());
			font1.draw(batch,items[i].getLabel(),items[i].getXpos(),items[i].getYpos());
		}
		
		
		
		
	
		
		batch.end();

		handleInput(Gdx.input, Gdx.graphics.getDeltaTime());

		if (System.nanoTime() - lastTime > 1000000000) {
		//	Gdx.app.log("TerrainTest", "fps: " + Gdx.graphics.getFramesPerSecond());
			lastTime = System.nanoTime();
		}
	}

	private void handleInput (Input input, float delta) {
	
	}
	
	private class TextItem  {
		
		String label;
		float size;
		float alpha;
		boolean leftRight;
		float ypos;
		float dx;
		float startOffset;
		float time = 0;
		
		float screenWidth;
		float xpos;
		
		public float getXpos() {
			return xpos;
		}
		
		
		
		public TextItem(String label, float size, float alpha,
				boolean leftRight, float ypos, float dx, float startOffset,
				float screenWidth) {
			super();
			this.label = label;
			this.size = size;
			this.alpha = alpha;
			this.leftRight = leftRight;
			this.ypos = ypos;
			this.dx = dx;
			this.startOffset = startOffset;
			this.screenWidth = screenWidth;
		}




		public void compute() {
			
		  
			if (leftRight) {
				xpos = screenWidth + startOffset;
				xpos -= time * dx;
				
				if (xpos < -startOffset) {
					time = 0;
				}
				
			} else {
				xpos = -startOffset;
				xpos += time * dx;
				
				if (xpos > screenWidth+startOffset) {
					time = 0;
				}
			}
			
			time += 1f;
		}




		public String getLabel() {
			return label;
		}




		public float getSize() {
			return size;
		}




		public float getAlpha() {
			return alpha;
		}




		public boolean isLeftRight() {
			return leftRight;
		}




		public float getYpos() {
			return ypos;
		}




		public float getDx() {
			return dx;
		}




		public float getStartOffset() {
			return startOffset;
		}




		public float getTime() {
			return time;
		}




		public float getScreenWidth() {
			return screenWidth;
		}
		
	}
	

	final static class TerrainChunk {
		public final byte[] heightMap;
		public final short width;
		public final short height;
		public final float[] vertices;
		public final short[] indices;
		public final int vertexSize;

		public TerrainChunk (int width, int height, int vertexSize) {
			if ((width + 1) * (height + 1) > Short.MAX_VALUE)
				throw new IllegalArgumentException("Chunk size too big, (width + 1)*(height+1) must be <= 32767");

			this.heightMap = new byte[(width + 1) * (height + 1)];
			this.width = (short)width;
			this.height = (short)height;
			this.vertices = new float[heightMap.length * vertexSize];
			this.indices = new short[width * height * 6];
			this.vertexSize = vertexSize;

			buildIndices();
			buildVertices();
		}

		public void animateVertices(double d) {
		
			double offset = 0;
			double stepH = Math.PI/20d;
			double stepW = 2.5d*Math.PI/(double)width;
			
			double hoff = 0;
			double woff = 0;
			
			for (int h = 0; h < height; h++) {
				
				hoff += stepH;
				woff = 0;
				for (int w = 0; w < width; w++) {
					
					float v = (float)(Math.sin(hoff+woff+d)*0.4d);
					
					int index = (h*width+w)*vertexSize+1;
					
					vertices[index] = v;
					
					woff += stepW;
				}
			}
			
		}
		
		public void buildVertices () {
			int heightPitch = height + 1;
			int widthPitch = width + 1;

			int idx = 0;
			int hIdx = 0;
			//int inc = vertexSize - 3;

			for (int z = 0; z < heightPitch; z++) {
				for (int x = 0; x < widthPitch; x++) {
					vertices[idx++] = x;
					vertices[idx++] = heightMap[hIdx++];
					vertices[idx++] = z;
					idx++;
					vertices[idx++] = x*(0.5f/(float)widthPitch);
					vertices[idx++] = z*(1f/(float)heightPitch);
					//idx += inc;
				}
			}
		}

		private void buildIndices () {
			int idx = 0;
			short pitch = (short)(width + 1);
			short i1 = 0;
			short i2 = 1;
			short i3 = (short)(1 + pitch);
			short i4 = pitch;

			short row = 0;

			for (int z = 0; z < height; z++) {
				for (int x = 0; x < width; x++) {
					indices[idx++] = i1;
					indices[idx++] = i2;
					indices[idx++] = i3;

					indices[idx++] = i3;
					indices[idx++] = i4;
					indices[idx++] = i1;

					i1++;
					i2++;
					i3++;
					i4++;
				}

				row += pitch;
				i1 = row;
				i2 = (short)(row + 1);
				i3 = (short)(i2 + pitch);
				i4 = (short)(row + pitch);
			}
		}
	}

	public boolean needsGL20 () {
		return false;
	}

	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void resize(int w, int h) {
	
		for (int i = 0;  i < texts.length; i++) {
			items[i] = new TextItem(texts[i], (float)(Math.random()*5d+1d), (float)(0.1d + Math.random()*0.3d),
					(i%2==1?true:false), (float)(h-i*(double)(h/(double)texts.length)), (float)(Math.random()*3d+1d), (float)(Math.random()*1000d+800d),
					w); 
		}
		
		height = h;
		width = w;
	}

	public void resume() {
		// TODO Auto-generated method stub
		
	}

	public void offsetChange(float arg0, float arg1, float arg2, float arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		
	}

	public void setIsPreview(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
}