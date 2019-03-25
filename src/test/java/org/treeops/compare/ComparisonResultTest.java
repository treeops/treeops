package org.treeops.compare;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"classpath:org/treeops/compare/compare.feature"})
public class ComparisonResultTest {

}
