/*
 Pilotix : a multiplayer piloting game.
 Copyright (C) 2003 Pilotix.Org

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.client.j3d;

import org.pilotix.client.*;
//import org.pilotix.common.*;
import org.w3c.dom.*;

import java.util.StringTokenizer;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

import javax.media.j3d.*;

import java.lang.Integer;

/**
 * Cette classe sert à lire le fichier de Forme3D du client écrit en XML.
 */
public class Shape3DHandler {

    private Document document;
    private Element rootNode;

    private int vertexCount;

    private Color3f color;

    private Element faceList;

    private NodeList faces;
    private Element face;

    private NodeList vertexs;
    private StringTokenizer vertexIds;
    private Element vertex;

    /**
     * Renvoie une instance de Shape3D correspondant aux données contenues dans
     * le fichier XML dont le nom est passé en paramètre et qui doit se trouver
     * dans le répertoire data/shapes.
     *
     * @param aShapeFile
     *            nom du fichier XML à traiter
     */
    public Shape3D getShape3DFromURL(String aShapeFile) {
        document = Environment.theXMLHandler
                .getDocumentFromURL(Environment.theRL.getResource(
                        ResourceLocator.SHAPE, aShapeFile));
        rootNode = document.getDocumentElement();

        //calcul du nombre de vertex utilisé :
        //  = nb face * 3
        int count = 0;
        NodeList faceLists = rootNode.getElementsByTagName("FaceList");
        for (int i = 0; i < faceLists.getLength(); i++) {
            NodeList faces = ((Element) faceLists.item(i))
                    .getElementsByTagName("Face");
            count = count + faces.getLength();
        }
        System.out.println("[Shape3DLoader] Total faces : " + count);
        TriangleArray triangleArray = new TriangleArray(count * 3,
                GeometryArray.COORDINATES
                | GeometryArray.NORMALS
                | GeometryArray.COLOR_3);

        //recuperation de tous les vertex
        vertexs = rootNode.getElementsByTagName("Point");
        vertexCount = 0;
        //pour chaque FaceList existant
        faceLists = rootNode.getElementsByTagName("FaceList");
        //System.out.println("[Shape3DLoader] nb FaceList : "+
        // faceLists.getLength());
        for (int i = 0; i < faceLists.getLength(); i++) {
            faceList = (Element) faceLists.item(i);
            //creation de la couleur associee :
            color = new Color3f(Float.parseFloat(faceList.getAttribute("R")),
                                Float.parseFloat(faceList.getAttribute("G")),
                                Float.parseFloat(faceList.getAttribute("B")));
            faces = faceList.getElementsByTagName("Face");
            for (int j = 0; j < faces.getLength(); j++) {
                face = (Element) faces.item(j);
                vertexIds = new StringTokenizer(face.getAttribute("PointIds"));
                while (vertexIds.hasMoreTokens()) {
                    vertex = (Element) (vertexs.item(Integer.parseInt(vertexIds
                            .nextToken())));
                    triangleArray.setCoordinate(vertexCount,
                             new Point3f(
                                  Float.parseFloat(vertex.getAttribute("X")),
                                  Float.parseFloat(vertex.getAttribute("Y")),
                                  Float.parseFloat(vertex.getAttribute("Z"))));
                    triangleArray.setColor(vertexCount, color);
                    vertexCount++;
                }
            }
        }

        GeometryInfo geometryInfo = new GeometryInfo(triangleArray);
        NormalGenerator normalGenerator = new NormalGenerator(0.1);
        normalGenerator.generateNormals(geometryInfo);
        Stripifier stripifier = new Stripifier();
        stripifier.stripify(geometryInfo);

        Appearance appearance = new Appearance();
        Material material = new Material();
        material.setColorTarget(Material.EMISSIVE);
        appearance.setMaterial(material);

        Shape3D theShape3D = new Shape3D();
        theShape3D.setGeometry(geometryInfo.getGeometryArray());
        theShape3D.setAppearance(appearance);
        return theShape3D;
    }
}
