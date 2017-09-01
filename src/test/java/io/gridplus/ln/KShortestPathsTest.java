package io.gridplus.ln;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jgrapht.GraphPath;
import org.junit.Test;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNPathValidator;
import io.gridplus.ln.model.LNVertex;

public class KShortestPathsTest extends NetworkTopologyTest {

	@Test
	public void testKShortestPaths1() {
		LNVertex v1 = new LNVertex(0);
		LNVertex v2 = new LNVertex(4);
		LNPathValidator pathValidator = new LNPathValidator(5);

		List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2, pathValidator);
		assertEquals("Paths found", 1, paths.size());
		GraphPath<LNVertex, LNEdge> path0 = paths.get(0);

		assertEquals("VStart", path0.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd", path0.getEndVertex(), new LNVertex(4));

		List<LNEdge> edges = path0.getEdgeList();
		assertEquals("Path0 size", 2, edges.size());
		assertEquals("V0-Source", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target", edges.get(0).getTarget(), new LNVertex(3));

		assertEquals("V1-Source", edges.get(1).getSource(), new LNVertex(3));
		assertEquals("V1-Target", edges.get(1).getTarget(), new LNVertex(4));

	}

	@Test
	public void testKShortestPaths2() {
		LNVertex v1 = new LNVertex(0);
		LNVertex v2 = new LNVertex(4);
		LNPathValidator pathValidator = new LNPathValidator(4);
		List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2,  pathValidator);
		assertEquals("Paths found", 2, paths.size());
		GraphPath<LNVertex, LNEdge> path0 = paths.get(0);
		GraphPath<LNVertex, LNEdge> path1 = paths.get(1);

		assertEquals("VStart p0", path0.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd p0", path0.getEndVertex(), new LNVertex(4));

		List<LNEdge> edges = path0.getEdgeList();
		assertEquals("Path0 size", 2, edges.size());
		assertEquals("V0-Source p0", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target p0", edges.get(0).getTarget(), new LNVertex(3));

		assertEquals("V1-Source p0", edges.get(1).getSource(), new LNVertex(3));
		assertEquals("V1-Target p0", edges.get(1).getTarget(), new LNVertex(4));

		assertEquals("VStart p1", path1.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd", path1.getEndVertex(), new LNVertex(4));

		edges = path1.getEdgeList();
		assertEquals("Path1 size", 3, edges.size());
		assertEquals("V0-Source p1", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target p1", edges.get(0).getTarget(), new LNVertex(2));

		assertEquals("V1-Source p1", edges.get(1).getSource(), new LNVertex(2));
		assertEquals("V1-Target p1", edges.get(1).getTarget(), new LNVertex(3));

		assertEquals("V2-Source p1", edges.get(2).getSource(), new LNVertex(3));
		assertEquals("V2-Target p1", edges.get(2).getTarget(), new LNVertex(4));

	}

	@Test
	public void testKShortestPaths3() {
		LNVertex v1 = new LNVertex(0);
		LNVertex v2 = new LNVertex(4);
		LNPathValidator pathValidator = new LNPathValidator(2);
		List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2, pathValidator);
		assertEquals("Paths found", 3, paths.size());
		GraphPath<LNVertex, LNEdge> path0 = paths.get(0);
		GraphPath<LNVertex, LNEdge> path1 = paths.get(1);
		GraphPath<LNVertex, LNEdge> path2 = paths.get(2);

		assertEquals("VStart p0", path0.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd p0", path0.getEndVertex(), new LNVertex(4));

		List<LNEdge> edges = path0.getEdgeList();
		assertEquals("Path0 size", 2, edges.size());
		assertEquals("V0-Source p0", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target p0", edges.get(0).getTarget(), new LNVertex(3));

		assertEquals("V1-Source p0", edges.get(1).getSource(), new LNVertex(3));
		assertEquals("V1-Target p0", edges.get(1).getTarget(), new LNVertex(4));

		assertEquals("VStart p1", path1.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd p1", path1.getEndVertex(), new LNVertex(4));

		edges = path1.getEdgeList();
		assertEquals("Path1 size", 3, edges.size());
		assertEquals("V0-Source p1", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target p1", edges.get(0).getTarget(), new LNVertex(2));

		assertEquals("V1-Source p1", edges.get(1).getSource(), new LNVertex(2));
		assertEquals("V1-Target p1", edges.get(1).getTarget(), new LNVertex(3));

		assertEquals("V2-Source p1", edges.get(2).getSource(), new LNVertex(3));
		assertEquals("V2-Target p1", edges.get(2).getTarget(), new LNVertex(4));

		assertEquals("VStart p2", path2.getStartVertex(), new LNVertex(0));
		assertEquals("VEnd p2", path2.getEndVertex(), new LNVertex(4));

		edges = path2.getEdgeList();
		assertEquals("Path1 size", 4, edges.size());
		assertEquals("V0-Source p2", edges.get(0).getSource(), new LNVertex(0));
		assertEquals("V0-Target p2", edges.get(0).getTarget(), new LNVertex(1));

		assertEquals("V1-Source p2", edges.get(1).getSource(), new LNVertex(1));
		assertEquals("V1-Target p2", edges.get(1).getTarget(), new LNVertex(2));

		assertEquals("V2-Source p2", edges.get(2).getSource(), new LNVertex(2));
		assertEquals("V2-Target p2", edges.get(2).getTarget(), new LNVertex(3));

		assertEquals("V3-Source p2", edges.get(3).getSource(), new LNVertex(3));
		assertEquals("V3-Target p2", edges.get(3).getTarget(), new LNVertex(4));
	}
}
