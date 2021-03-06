package com.jetbrains.python.refactoring.classes.membersManager;

import com.intellij.refactoring.RefactoringBundle;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.refactoring.classes.PyClassRefactoringUtil;
import com.jetbrains.python.refactoring.classes.ui.PyClassCellRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Plugin that moves superclasses from one class to another
 *
 * @author Ilya.Kazakevich
 */
class SuperClassesManager extends MembersManager<PyClass> {
  SuperClassesManager() {
    super(PyClass.class);
  }


  @NotNull
  @Override
  protected List<PyElement> getMembersCouldBeMoved(@NotNull final PyClass pyClass) {
    return Arrays.<PyElement>asList(pyClass.getSuperClasses());
  }

  @Override
  protected Collection<PyElement> moveMembers(@NotNull final PyClass from, @NotNull final Collection<PyClass> members, @NotNull final PyClass... to) {
    for (final PyClass destClass : to) {
      PyClassRefactoringUtil.addSuperclasses(from.getProject(), destClass, members.toArray(new PyClass[members.size()]));
    }

    for (final PyExpression expression : from.getSuperClassExpressions()) {
      for (final PyClass member : members) {
        if (expression.getText().equals(member.getName())) {
          expression.delete();
        }
      }
    }
    return Collections.emptyList(); //Hack: we know that "superclass expression" can't have reference
  }

  @NotNull
  @Override
  public PyMemberInfo apply(@NotNull final PyClass input) {
    final String name = RefactoringBundle.message("member.info.extends.0", PyClassCellRenderer.getClassText(input));
    //TODO: Check for "overrides"
    return new PyMemberInfo(input, false, name, false, this);
  }
}
