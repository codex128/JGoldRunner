/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.effects;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 *
 * @author gary
 */
public class ShatterFactory {
	
	private static final Logger LOG = Logger.getLogger(ShatterFactory.class.getName());
	
	/**
	 * Generate shard geometries matching the triangles composing
	 * the asserted geometry.
	 * <p>
	 * The asserted geometry will be removed from the scene.
	 * @param geometry
	 * @param maxShards maximum number of shards that may be produced.
	 * @return particle node containing the shard geometries as particles.
	 */
	public static Node shatter(Geometry geometry, int maxShards) {
		Node node = new Node("main shard node");
		node.setLocalTransform(geometry.getWorldTransform());
		Material mat = geometry.getMaterial().clone();
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		for (int index = 0; index < geometry.getMesh().getTriangleCount(); index++) {
			// get the triangle from the geometry's mesh
			Triangle tri = new Triangle();
			geometry.getMesh().getTriangle(index, tri);
			// alter triangle so that points are relative to the average
			Vector3f avg = average(tri.get1(), tri.get2(), tri.get3());
			for (int i = 0; i < 3; i++) {
				tri.get(i).subtractLocal(avg);
			}
			// shard mesh
			Mesh mesh = new Mesh();
			// define the verticies of the shard from the geometry's triangles
			Vector3f[] verts = new Vector3f[3];
			for (int i = 0; i < verts.length; i++) {
				verts[i] = tri.get(i);
			}
			// define the texture coordinates of the shard (randomly)
			Vector2f[] texcoord = new Vector2f[4];
			Vector2f low = new Vector2f(random(0, .8f), random(0, .8f));
			Vector2f hi = new Vector2f(random(low.x+.1f, 1), random(low.y+.1f, 1));
			texcoord[0] = new Vector2f(low.x, low.y);
			texcoord[1] = new Vector2f(hi.x, low.y);
			texcoord[2] = new Vector2f(low.x, hi.y);
			texcoord[3] = new Vector2f(hi.x, hi.y);
			// define the faces (by index) of the shard
			int[] indexes = { 0,1,2 };
			// create buffers for the mesh
			mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verts));
			mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoord));
			mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
			// update the mesh bounds
			mesh.updateBound();
			// create shard geometry
			Geometry shard = new Geometry("shard geometry", mesh);
			shard.setLocalTranslation(avg);
			// set material
			shard.setMaterial(mat.clone());
			if (mat.isTransparent()) {
				shard.setQueueBucket(RenderQueue.Bucket.Transparent);
			}
			// attach to scene
			node.attachChild(shard);
			// shard limit
			if (maxShards >= 0 && index >= maxShards) break;
		}
		geometry.getParent().attachChild(node);
		geometry.removeFromParent();
		return node;
 	}
	
	/**
	 * Generate shard geometries matching the triangles composing
	 * the asserted geometry.
	 * <p>
	 * The asserted geometry will be removed from the scene.
	 * @param geometry
	 * @return particle node containing the shard geometries as particles.
	 */
	public static Node shatter(Geometry geometry) {
		return shatter(geometry, -1);
	}
	
	/**
	 * Generate shard geometries matching the triangles composing
	 * the asserted geometry.
	 * <p>
	 * The asserted geometry will be removed from the scene.
	 * @param spatial converts to Geometry
	 * @param maxShards maximum number of shard geometries that may be produced.
	 * @return particle node containing the shard geometries as particles.
	 */
	public static Node shatter(Spatial spatial, int maxShards) {
		return shatter(getGeometry(spatial), maxShards);
	}
	
	/**
	 * Generate shard geometries matching the triangles composing
	 * the asserted geometry.
	 * <p>
	 * The asserted geometry will be removed from the scene.
	 * @param spatial converts to Geometry
	 * @return particle node containing the shard geometries as particles.
	 */
	public static Node shatter(Spatial spatial) {
		return shatter(getGeometry(spatial), -1);
	}
	
	
	/**
	 * Retrieves a Geometry from the asserted spatial.
	 * If the spatial is a Node, will retrieve a Geometry from its descendents (if able).
	 * @throws NullPointerException if no Geometry was found.
	 * @param spatial
	 * @return
	 */
	private static Geometry getGeometry(Spatial spatial) {
		if (spatial instanceof Geometry) return (Geometry)spatial;
		else if (spatial instanceof Node) {
			Node n = (Node)spatial;
			List<Geometry> list = n.descendantMatches(Geometry.class);
			if (!list.isEmpty()) {
				if (list.size() > 1) {
					LOG.warning("Node has multiple Geometry descendents");
				}
				return list.get(0);
			}
		}
		throw new NullPointerException("Failed to find geometry");
	}	
	
	/**
	 * Returns the average of all asserted vectors.
	 * @param vecs
	 * @return 
	 */
	private static Vector3f average(Vector3f... vecs) {
		Vector3f avg = new Vector3f();
		for (Vector3f vec : vecs) avg.addLocal(vec);
		return avg.divideLocal(vecs.length);
	}
	
	/**
	 * Returns a random single-precision number between min and max.
	 * @param min
	 * @param max
	 * @return 
	 */
	private static float random(float min, float max) {
		return (float)ThreadLocalRandom.current().nextDouble(min, max);
	}
	
}
