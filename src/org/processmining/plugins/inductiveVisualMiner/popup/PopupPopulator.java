package org.processmining.plugins.inductiveVisualMiner.popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerPanel;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerState;
import org.processmining.plugins.inductiveVisualMiner.alignment.LogMovePosition;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogMetrics;
import org.processmining.plugins.inductiveVisualMiner.performance.Performance;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotEdge;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotNode;

public class PopupPopulator {
	public static void updatePopup(InductiveVisualMinerPanel panel, InductiveVisualMinerState state)
			throws UnknownTreeNodeException {
		if (panel.getGraph().getMouseInElements().isEmpty()) {
			panel.getGraph().setShowPopup(false);
		} else {
			//output statistics about the node
			DotElement element = panel.getGraph().getMouseInElements().iterator().next();
			if (element instanceof LocalDotNode) {
				int unode = ((LocalDotNode) element).getUnode();
				if (state.isAlignmentReady() && state.getTree().isActivity(unode)) {
					List<String> popup = new ArrayList<>();

					//frequencies
					popup.add("number of occurrences " + IvMLogMetrics.getNumberOfTracesRepresented(state.getTree(),
							unode, false, state.getIvMLogInfoFiltered()));

					//waiting time
					if (state.isPerformanceReady()) {
						if (state.getPerformance().getWaitingTime(unode) > -0.1) {
							popup.add("average waiting time  "
									+ Performance.timeToString((long) state.getPerformance().getWaitingTime(unode)));
						} else {
							popup.add("average waiting time  -");
						}
					} else {
						popup.add(" ");
					}

					//queueing time
					if (state.isPerformanceReady()) {
						if (state.getPerformance().getQueueingTime(unode) > -0.1) {
							popup.add("average queueing time "
									+ Performance.timeToString((long) state.getPerformance().getQueueingTime(unode)));
						} else {
							popup.add("average queueing time -");
						}
					} else {
						popup.add(" ");
					}

					//service time
					if (state.isPerformanceReady()) {
						if (state.getPerformance().getServiceTime(unode) > -0.1) {
							popup.add("average service time  "
									+ Performance.timeToString((long) state.getPerformance().getServiceTime(unode)));
						} else {
							popup.add("average service time  -");
						}
					} else {
						popup.add(" ");
					}

					//sojourn time
					if (state.isPerformanceReady()) {
						if (state.getPerformance().getSojournTime(unode) > -0.1) {
							popup.add("average sojourn time  "
									+ Performance.timeToString((long) state.getPerformance().getSojournTime(unode)));
						} else {
							popup.add("average sojourn time  -");
						}
					} else {
						popup.add(" ");
					}

					panel.getGraph().setPopupActivity(popup, unode);
					panel.getGraph().setShowPopup(true);
				} else {
					panel.getGraph().setShowPopup(false);
				}
			} else if (state.getVisualisationInfo() != null && element instanceof LocalDotEdge
					&& state.getVisualisationInfo().getAllLogMoveEdges().contains(element)) {
				//log move edge
				LocalDotEdge edge = (LocalDotEdge) element;
				int maxNumberOfLogMoves = 10;
				if (state.isAlignmentReady()) {
					List<String> popup = new ArrayList<>();
					LogMovePosition position = LogMovePosition.of(edge);
					MultiSet<XEventClass> logMoves = IvMLogMetrics.getLogMoves(position, state.getIvMLogInfoFiltered());

					popup.add(logMoves.size() + (logMoves.size() <= 1 ? " event" : " events")
							+ " additional to the model:");

					//get digits of the maximum cardinality
					long max = logMoves.getCardinalityOf(logMoves.getElementWithHighestCardinality());
					int maxDigits = (int) (Math.log10(max) + 1);

					if (max == 0) {
						panel.getGraph().setShowPopup(false);
						return;
					}

					List<XEventClass> activities = logMoves.sortByCardinality();
					Collections.reverse(activities);
					for (XEventClass activity : activities) {
						if (maxNumberOfLogMoves > 0) {
							popup.add(String.format("%" + maxDigits + "d", logMoves.getCardinalityOf(activity)) + " "
									+ StringUtils.abbreviate(activity.toString(), 40 - maxDigits));
						}
						maxNumberOfLogMoves--;
					}
					if (maxNumberOfLogMoves < 0) {
						popup.add("... and " + Math.abs(maxNumberOfLogMoves) + " activities more");
					}

					panel.getGraph().setPopupLogMove(popup, position);
					panel.getGraph().setShowPopup(true);
				} else {
					panel.getGraph().setShowPopup(false);
				}
			} else if (state.getVisualisationInfo() != null && element instanceof LocalDotEdge
					&& state.getVisualisationInfo().getAllModelMoveEdges().contains(element)) {
				//model move edge
				if (state.isAlignmentReady()) {
					LocalDotEdge edge = (LocalDotEdge) element;
					int node = edge.getUnode();
					List<String> popup = new ArrayList<>();
					popup.add(IvMLogMetrics.getModelMovesLocal(node, state.getIvMLogInfoFiltered())
							+ " times, activity ");
					popup.add(StringUtils.abbreviate(state.getTree().getActivityName(edge.getUnode()), 40));
					popup.add("was not executed.");

					panel.getGraph().setPopupActivity(popup, -1);
					panel.getGraph().setShowPopup(true);
				} else {
					panel.getGraph().setShowPopup(false);
				}
			} else {
				panel.getGraph().setShowPopup(false);
			}
		}
	}
}
