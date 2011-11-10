package graphics.model.geom;

import org.lwjgl.opengl.GL11;

public class Triangle extends Face{
	public Triangle(){
		vertices = new float[3][3];
		normals = new float[3][3];
	}

	@Override
	public void draw() {
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			for (int i = 0; i < vertices.length; i++) {
				float[] normal = normals[i];
				float[] vertex = vertices[i];

				GL11.glNormal3f(normal[0], normal[1], normal[2]);
				GL11.glVertex3f(vertex[0], vertex[1], vertex[2]);
			}
		}
		GL11.glEnd();
	}
}