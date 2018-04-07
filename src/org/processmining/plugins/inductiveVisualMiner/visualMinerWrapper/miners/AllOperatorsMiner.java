package org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners;

import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMfa;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerParameters;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.processtree.ProcessTree;

public class AllOperatorsMiner extends VisualMinerWrapper {

	public String toString() {
		return "all operators miner (IMfa)";
	}

	public ProcessTree mine(IMLog log, VisualMinerParameters parameters, final IvMCanceller canceller) {

		//copy the relevant parameters
		MiningParameters miningParameters = new MiningParametersIMfa();
		miningParameters.setNoiseThreshold((float) (1 - parameters.getPaths()));

		return IMProcessTree.mineProcessTree(log, miningParameters, new Canceller() {

			public boolean isCancelled() {
				return canceller.isCancelled();
			}
		});
	}

}
