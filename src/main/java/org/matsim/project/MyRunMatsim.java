/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.project;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;

/**
 * @author nagel
 *
 */
public class MyRunMatsim {

	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/equil/config.xml" } ;
			// to make sure that something is run by default; better start from MATSimGUI.
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}

		Config config = ConfigUtils.loadConfig( args ) ;
		
		// possibly modify config here
		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(1);
		// ---
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		
		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		
		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		controler.addOverridingModule(new MyContribModule(config));
		// ---
		
		controler.run();
	}

	private static class MyScoringFunctionFactory implements ScoringFunctionFactory {

		private final Config config;

		@Inject Scenario scenario;

		@Inject
		MyScoringFunctionFactory( Config config ) {
			this.config = config;
		}

		@Override
		public ScoringFunction createNewScoringFunction( Person person ) {
			System.err.println( "blabla=" + config.controler().getRoutingAlgorithmType());
			System.err.println( "scenario=" + scenario.getNetwork().getLinks().values().iterator().next());
			return null;
		}
	}

	private static class MyEventHandler implements EventHandler {

	}

	private static class MyContribModule extends AbstractModule {
		private final Config config;

		public MyContribModule(Config config) {
			this.config = config;
		}

		@Override
		public void install() {
			//bind(ScoringFunctionFactory.class).to(MyScoringFunctionFactory.class);
			bind(ScoringFunctionFactory.class).toInstance( new MyScoringFunctionFactory(config));
			this.addEventHandlerBinding().to( MyEventHandler.class );
		}
	}
}
