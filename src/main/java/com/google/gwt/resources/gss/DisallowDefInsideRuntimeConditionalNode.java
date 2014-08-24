/*
 * Copyright 2014 Julien Dramaix.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.resources.gss;

import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.common.css.compiler.ast.CssDefinitionNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.ErrorManager;
import com.google.common.css.compiler.ast.GssError;
import com.google.common.css.compiler.ast.VisitController;

import java.util.Stack;

public class DisallowDefInsideRuntimeConditionalNode extends DefaultTreeVisitor implements
    CssCompilerPass {

  private final VisitController visitController;
  private final ErrorManager errorManager;

  private Stack<CssConditionalRuleNode> cssConditionalRuleNodes;

  public DisallowDefInsideRuntimeConditionalNode(VisitController visitController,
      ErrorManager errorManager) {
    this.visitController = visitController;
    this.errorManager = errorManager;
  }

  @Override
  public boolean enterDefinition(CssDefinitionNode node) {
    if (inConditionalRule()) {
      errorManager.report(new GssError("You cannot define a constant inside a ConditionalNode " +
          "that will be evaluated at runtime.", node.getSourceCodeLocation()));
    }
    return false;
  }

  @Override
  public boolean enterConditionalRule(CssConditionalRuleNode node) {
    cssConditionalRuleNodes.push(node);
    return true;
  }

  @Override
  public void leaveConditionalRule(CssConditionalRuleNode node) {
    cssConditionalRuleNodes.pop();
  }

  @Override
  public void runPass() {
    cssConditionalRuleNodes = new Stack<CssConditionalRuleNode>();

    visitController.startVisit(this);
  }

  private boolean inConditionalRule() {
    return !cssConditionalRuleNodes.isEmpty();
  }
}