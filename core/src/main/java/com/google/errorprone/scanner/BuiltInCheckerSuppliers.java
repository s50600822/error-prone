/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.scanner;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.errorprone.BugCheckerInfo;
import com.google.errorprone.bugpatterns.AmbiguousMethodReference;
import com.google.errorprone.bugpatterns.ArgumentParameterMismatch;
import com.google.errorprone.bugpatterns.ArgumentParameterSwap;
import com.google.errorprone.bugpatterns.ArrayEquals;
import com.google.errorprone.bugpatterns.ArrayHashCode;
import com.google.errorprone.bugpatterns.ArrayToString;
import com.google.errorprone.bugpatterns.ArraysAsListPrimitiveArray;
import com.google.errorprone.bugpatterns.AssertFalse;
import com.google.errorprone.bugpatterns.AsyncFunctionReturnsNull;
import com.google.errorprone.bugpatterns.BadAnnotationImplementation;
import com.google.errorprone.bugpatterns.BadComparable;
import com.google.errorprone.bugpatterns.BadShiftAmount;
import com.google.errorprone.bugpatterns.BigDecimalLiteralDouble;
import com.google.errorprone.bugpatterns.BoxedPrimitiveConstructor;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.CannotMockFinalClass;
import com.google.errorprone.bugpatterns.ChainingConstructorIgnoresParameter;
import com.google.errorprone.bugpatterns.CheckReturnValue;
import com.google.errorprone.bugpatterns.ClassCanBeStatic;
import com.google.errorprone.bugpatterns.ClassName;
import com.google.errorprone.bugpatterns.ClassNewInstance;
import com.google.errorprone.bugpatterns.ComparisonContractViolated;
import com.google.errorprone.bugpatterns.ComparisonOutOfRange;
import com.google.errorprone.bugpatterns.CompileTimeConstantChecker;
import com.google.errorprone.bugpatterns.ConstantField;
import com.google.errorprone.bugpatterns.ConstantOverflow;
import com.google.errorprone.bugpatterns.DeadException;
import com.google.errorprone.bugpatterns.DefaultCharset;
import com.google.errorprone.bugpatterns.DepAnn;
import com.google.errorprone.bugpatterns.DivZero;
import com.google.errorprone.bugpatterns.ElementsCountedInLoop;
import com.google.errorprone.bugpatterns.EmptyIfStatement;
import com.google.errorprone.bugpatterns.EmptyTopLevelDeclaration;
import com.google.errorprone.bugpatterns.EqualsHashCode;
import com.google.errorprone.bugpatterns.EqualsIncompatibleType;
import com.google.errorprone.bugpatterns.EqualsNaN;
import com.google.errorprone.bugpatterns.Finally;
import com.google.errorprone.bugpatterns.ForOverrideChecker;
import com.google.errorprone.bugpatterns.FunctionalInterfaceClash;
import com.google.errorprone.bugpatterns.FunctionalInterfaceMethodChanged;
import com.google.errorprone.bugpatterns.FuturesGetCheckedIllegalExceptionType;
import com.google.errorprone.bugpatterns.GetClassOnAnnotation;
import com.google.errorprone.bugpatterns.GetClassOnClass;
import com.google.errorprone.bugpatterns.GetClassOnEnum;
import com.google.errorprone.bugpatterns.GuavaSelfEquals;
import com.google.errorprone.bugpatterns.HashtableContains;
import com.google.errorprone.bugpatterns.IdentityBinaryExpression;
import com.google.errorprone.bugpatterns.ImmutableModification;
import com.google.errorprone.bugpatterns.IncompatibleModifiersChecker;
import com.google.errorprone.bugpatterns.InfiniteRecursion;
import com.google.errorprone.bugpatterns.InputStreamSlowMultibyteRead;
import com.google.errorprone.bugpatterns.InsecureCipherMode;
import com.google.errorprone.bugpatterns.InvalidPatternSyntax;
import com.google.errorprone.bugpatterns.IsInstanceOfClass;
import com.google.errorprone.bugpatterns.IterableAndIterator;
import com.google.errorprone.bugpatterns.JMockTestWithoutRunWithOrRuleAnnotation;
import com.google.errorprone.bugpatterns.JUnit3FloatingPointComparisonWithoutDelta;
import com.google.errorprone.bugpatterns.JUnit3TestNotRun;
import com.google.errorprone.bugpatterns.JUnit4SetUpNotRun;
import com.google.errorprone.bugpatterns.JUnit4TearDownNotRun;
import com.google.errorprone.bugpatterns.JUnit4TestNotRun;
import com.google.errorprone.bugpatterns.JUnitAmbiguousTestClass;
import com.google.errorprone.bugpatterns.LongLiteralLowerCaseSuffix;
import com.google.errorprone.bugpatterns.MissingCasesInEnumSwitch;
import com.google.errorprone.bugpatterns.MissingFail;
import com.google.errorprone.bugpatterns.MissingOverride;
import com.google.errorprone.bugpatterns.MisusedWeekYear;
import com.google.errorprone.bugpatterns.MixedArrayDimensions;
import com.google.errorprone.bugpatterns.MockitoCast;
import com.google.errorprone.bugpatterns.MockitoUsage;
import com.google.errorprone.bugpatterns.ModifyingCollectionWithItself;
import com.google.errorprone.bugpatterns.MultiVariableDeclaration;
import com.google.errorprone.bugpatterns.MultipleTopLevelClasses;
import com.google.errorprone.bugpatterns.NarrowingCompoundAssignment;
import com.google.errorprone.bugpatterns.NoAllocationChecker;
import com.google.errorprone.bugpatterns.NonAtomicVolatileUpdate;
import com.google.errorprone.bugpatterns.NonCanonicalStaticImport;
import com.google.errorprone.bugpatterns.NonCanonicalStaticMemberImport;
import com.google.errorprone.bugpatterns.NonFinalCompileTimeConstant;
import com.google.errorprone.bugpatterns.NonOverridingEquals;
import com.google.errorprone.bugpatterns.NonRuntimeAnnotation;
import com.google.errorprone.bugpatterns.NullableConstructor;
import com.google.errorprone.bugpatterns.NullablePrimitive;
import com.google.errorprone.bugpatterns.NullableVoid;
import com.google.errorprone.bugpatterns.NumericEquality;
import com.google.errorprone.bugpatterns.OperatorPrecedence;
import com.google.errorprone.bugpatterns.OptionalEquality;
import com.google.errorprone.bugpatterns.Overrides;
import com.google.errorprone.bugpatterns.PackageInfo;
import com.google.errorprone.bugpatterns.PackageLocation;
import com.google.errorprone.bugpatterns.PreconditionsCheckNotNull;
import com.google.errorprone.bugpatterns.PreconditionsCheckNotNullPrimitive;
import com.google.errorprone.bugpatterns.PreconditionsInvalidPlaceholder;
import com.google.errorprone.bugpatterns.PrimitiveArrayPassedToVarargsMethod;
import com.google.errorprone.bugpatterns.PrivateConstructorForUtilityClass;
import com.google.errorprone.bugpatterns.ProtoFieldNullComparison;
import com.google.errorprone.bugpatterns.ProtoFieldPreconditionsCheckNotNull;
import com.google.errorprone.bugpatterns.ProtoStringFieldReferenceEquality;
import com.google.errorprone.bugpatterns.RandomModInteger;
import com.google.errorprone.bugpatterns.RedundantThrows;
import com.google.errorprone.bugpatterns.ReferenceEquality;
import com.google.errorprone.bugpatterns.RemoveUnusedImports;
import com.google.errorprone.bugpatterns.RequiredModifiersChecker;
import com.google.errorprone.bugpatterns.RestrictedApiChecker;
import com.google.errorprone.bugpatterns.ReturnValueIgnored;
import com.google.errorprone.bugpatterns.SelfAssignment;
import com.google.errorprone.bugpatterns.SelfComparison;
import com.google.errorprone.bugpatterns.SelfEquality;
import com.google.errorprone.bugpatterns.SelfEquals;
import com.google.errorprone.bugpatterns.SizeGreaterThanOrEqualsZero;
import com.google.errorprone.bugpatterns.StaticAccessedFromInstance;
import com.google.errorprone.bugpatterns.StreamToString;
import com.google.errorprone.bugpatterns.StringBuilderInitWithChar;
import com.google.errorprone.bugpatterns.StringEquality;
import com.google.errorprone.bugpatterns.SuppressWarningsDeprecated;
import com.google.errorprone.bugpatterns.ThrowIfUncheckedKnownChecked;
import com.google.errorprone.bugpatterns.ThrowsUncheckedException;
import com.google.errorprone.bugpatterns.TruthConstantAsserts;
import com.google.errorprone.bugpatterns.TruthSelfEquals;
import com.google.errorprone.bugpatterns.TryFailThrowable;
import com.google.errorprone.bugpatterns.TypeParameterQualifier;
import com.google.errorprone.bugpatterns.TypeParameterUnusedInFormals;
import com.google.errorprone.bugpatterns.UnnecessaryStaticImport;
import com.google.errorprone.bugpatterns.UnnecessaryTypeArgument;
import com.google.errorprone.bugpatterns.UnsynchronizedOverridesSynchronized;
import com.google.errorprone.bugpatterns.UnusedAnonymousClass;
import com.google.errorprone.bugpatterns.UnusedCollectionModifiedInPlace;
import com.google.errorprone.bugpatterns.VarChecker;
import com.google.errorprone.bugpatterns.WaitNotInLoop;
import com.google.errorprone.bugpatterns.WildcardImport;
import com.google.errorprone.bugpatterns.WrongParameterPackage;
import com.google.errorprone.bugpatterns.android.FragmentNotInstantiable;
import com.google.errorprone.bugpatterns.android.HardCodedSdCardPath;
import com.google.errorprone.bugpatterns.android.IsLoggableTagLength;
import com.google.errorprone.bugpatterns.android.MislabeledAndroidString;
import com.google.errorprone.bugpatterns.android.RectIntersectReturnValueIgnored;
import com.google.errorprone.bugpatterns.android.StaticOrDefaultInterfaceMethod;
import com.google.errorprone.bugpatterns.collectionincompatibletype.CollectionIncompatibleType;
import com.google.errorprone.bugpatterns.collectionincompatibletype.CompatibleWithMisuse;
import com.google.errorprone.bugpatterns.collectionincompatibletype.IncompatibleArgumentType;
import com.google.errorprone.bugpatterns.formatstring.FormatString;
import com.google.errorprone.bugpatterns.formatstring.FormatStringAnnotationChecker;
import com.google.errorprone.bugpatterns.inject.AssistedInjectAndInjectOnConstructors;
import com.google.errorprone.bugpatterns.inject.AssistedInjectAndInjectOnSameConstructor;
import com.google.errorprone.bugpatterns.inject.AutoFactoryAtInject;
import com.google.errorprone.bugpatterns.inject.InjectOnConstructorOfAbstractClass;
import com.google.errorprone.bugpatterns.inject.InjectedConstructorAnnotations;
import com.google.errorprone.bugpatterns.inject.InvalidTargetingOnScopingAnnotation;
import com.google.errorprone.bugpatterns.inject.JavaxInjectOnAbstractMethod;
import com.google.errorprone.bugpatterns.inject.JavaxInjectOnFinalField;
import com.google.errorprone.bugpatterns.inject.MoreThanOneInjectableConstructor;
import com.google.errorprone.bugpatterns.inject.MoreThanOneQualifier;
import com.google.errorprone.bugpatterns.inject.MoreThanOneScopeAnnotationOnClass;
import com.google.errorprone.bugpatterns.inject.OverlappingQualifierAndScopeAnnotation;
import com.google.errorprone.bugpatterns.inject.QualifierOnMethodWithoutProvides;
import com.google.errorprone.bugpatterns.inject.QualifierWithTypeUse;
import com.google.errorprone.bugpatterns.inject.ScopeAnnotationOnInterfaceOrAbstractClass;
import com.google.errorprone.bugpatterns.inject.ScopeOrQualifierAnnotationRetention;
import com.google.errorprone.bugpatterns.inject.dagger.EmptySetMultibindingContributions;
import com.google.errorprone.bugpatterns.inject.dagger.MultibindsInsteadOfMultibindings;
import com.google.errorprone.bugpatterns.inject.dagger.PrivateConstructorForNoninstantiableModule;
import com.google.errorprone.bugpatterns.inject.dagger.ProvidesNull;
import com.google.errorprone.bugpatterns.inject.dagger.UseBinds;
import com.google.errorprone.bugpatterns.inject.guice.AssistedInjectScoping;
import com.google.errorprone.bugpatterns.inject.guice.AssistedParameters;
import com.google.errorprone.bugpatterns.inject.guice.BindingToUnqualifiedCommonType;
import com.google.errorprone.bugpatterns.inject.guice.InjectOnFinalField;
import com.google.errorprone.bugpatterns.inject.guice.OverridesGuiceInjectableMethod;
import com.google.errorprone.bugpatterns.inject.guice.OverridesJavaxInjectableMethod;
import com.google.errorprone.bugpatterns.inject.guice.ProvidesMethodOutsideOfModule;
import com.google.errorprone.bugpatterns.threadsafety.DoubleCheckedLocking;
import com.google.errorprone.bugpatterns.threadsafety.GuardedByChecker;
import com.google.errorprone.bugpatterns.threadsafety.GuardedByValidator;
import com.google.errorprone.bugpatterns.threadsafety.ImmutableChecker;
import com.google.errorprone.bugpatterns.threadsafety.ImmutableEnumChecker;
import com.google.errorprone.bugpatterns.threadsafety.LockMethodChecker;
import com.google.errorprone.bugpatterns.threadsafety.StaticGuardedByInstance;
import com.google.errorprone.bugpatterns.threadsafety.SynchronizeOnNonFinalField;
import com.google.errorprone.bugpatterns.threadsafety.UnlockMethodChecker;

/**
 * Static helper class that provides {@link ScannerSupplier}s and {@link BugChecker}s for the
 * built-in Error Prone checks, as opposed to plugin checks or checks used in tests.
 */
public class BuiltInCheckerSuppliers {
  @SafeVarargs
  public static ImmutableSet<BugCheckerInfo> getSuppliers(Class<? extends BugChecker>... checkers) {
    ImmutableSet.Builder<BugCheckerInfo> result = ImmutableSet.builder();
    for (Class<? extends BugChecker> checker : checkers) {
      result.add(BugCheckerInfo.create(checker));
    }
    return result.build();
  }

  /** Returns a {@link ScannerSupplier} with all {@link BugChecker}s in Error Prone. */
  public static ScannerSupplier allChecks() {
    return ScannerSupplier.fromBugCheckerInfos(
        Iterables.concat(ENABLED_ERRORS, ENABLED_WARNINGS, DISABLED_CHECKS));
  }

  /**
   * Returns a {@link ScannerSupplier} with the {@link BugChecker}s that are in the ENABLED lists.
   */
  public static ScannerSupplier defaultChecks() {
    return allChecks()
        .filter(Predicates.or(Predicates.in(ENABLED_ERRORS), Predicates.in(ENABLED_WARNINGS)));
  }

  /**
   * Returns a {@link ScannerSupplier} with the {@link BugChecker}s that are in the ENABLED_ERRORS
   * list.
   */
  public static ScannerSupplier errorChecks() {
    return allChecks().filter(Predicates.in(ENABLED_ERRORS));
  }

  /** A list of all checks with severity ERROR that are on by default. */
  public static final ImmutableSet<BugCheckerInfo> ENABLED_ERRORS =
      getSuppliers(
          ArrayEquals.class,
          ArrayHashCode.class,
          ArrayToString.class,
          ArraysAsListPrimitiveArray.class,
          AssistedInjectScoping.class,
          AssistedParameters.class,
          AsyncFunctionReturnsNull.class,
          BadShiftAmount.class,
          ChainingConstructorIgnoresParameter.class,
          CheckReturnValue.class,
          CollectionIncompatibleType.class,
          CompatibleWithMisuse.class,
          ComparisonOutOfRange.class,
          CompileTimeConstantChecker.class,
          ConstantOverflow.class,
          DeadException.class,
          EqualsNaN.class,
          ForOverrideChecker.class,
          FormatString.class,
          FormatStringAnnotationChecker.class,
          FunctionalInterfaceMethodChanged.class,
          FuturesGetCheckedIllegalExceptionType.class,
          GetClassOnAnnotation.class,
          GetClassOnClass.class,
          GuardedByChecker.class,
          GuardedByValidator.class,
          GuavaSelfEquals.class,
          HashtableContains.class,
          IdentityBinaryExpression.class,
          ImmutableChecker.class,
          ImmutableModification.class,
          IncompatibleArgumentType.class,
          InfiniteRecursion.class,
          InjectOnFinalField.class,
          InsecureCipherMode.class,
          InvalidPatternSyntax.class,
          IsInstanceOfClass.class,
          IsLoggableTagLength.class,
          JavaxInjectOnAbstractMethod.class,
          JUnit3TestNotRun.class,
          JUnit4SetUpNotRun.class,
          JUnit4TearDownNotRun.class,
          JUnit4TestNotRun.class,
          MislabeledAndroidString.class,
          MisusedWeekYear.class,
          MockitoCast.class,
          MockitoUsage.class,
          ModifyingCollectionWithItself.class,
          MoreThanOneInjectableConstructor.class,
          MoreThanOneScopeAnnotationOnClass.class,
          NonCanonicalStaticImport.class,
          NonFinalCompileTimeConstant.class,
          OptionalEquality.class,
          OverlappingQualifierAndScopeAnnotation.class,
          Overrides.class,
          OverridesJavaxInjectableMethod.class,
          PackageInfo.class,
          PreconditionsCheckNotNull.class,
          PreconditionsCheckNotNullPrimitive.class,
          ProtoFieldNullComparison.class,
          ProvidesMethodOutsideOfModule.class,
          ProvidesNull.class,
          RandomModInteger.class,
          RectIntersectReturnValueIgnored.class,
          RestrictedApiChecker.class,
          ReturnValueIgnored.class,
          SelfAssignment.class,
          SelfComparison.class,
          SelfEquals.class,
          SelfEquality.class,
          SizeGreaterThanOrEqualsZero.class,
          StreamToString.class,
          StringBuilderInitWithChar.class,
          SuppressWarningsDeprecated.class,
          ThrowIfUncheckedKnownChecked.class,
          TryFailThrowable.class,
          TypeParameterQualifier.class,
          UnnecessaryTypeArgument.class,
          UnusedAnonymousClass.class,
          UnusedCollectionModifiedInPlace.class);

  /** A list of all checks with severity WARNING that are on by default. */
  public static final ImmutableSet<BugCheckerInfo> ENABLED_WARNINGS =
      getSuppliers(
          AmbiguousMethodReference.class,
          BadAnnotationImplementation.class,
          BadComparable.class,
          BoxedPrimitiveConstructor.class,
          CannotMockFinalClass.class,
          ClassCanBeStatic.class,
          ClassNewInstance.class,
          DefaultCharset.class,
          DoubleCheckedLocking.class,
          ElementsCountedInLoop.class,
          EqualsHashCode.class,
          EqualsIncompatibleType.class,
          Finally.class,
          FragmentNotInstantiable.class,
          FunctionalInterfaceClash.class,
          GetClassOnEnum.class,
          ImmutableEnumChecker.class,
          IncompatibleModifiersChecker.class,
          InjectOnConstructorOfAbstractClass.class,
          InputStreamSlowMultibyteRead.class,
          IterableAndIterator.class,
          JUnit3FloatingPointComparisonWithoutDelta.class,
          JUnitAmbiguousTestClass.class,
          MissingCasesInEnumSwitch.class,
          MissingFail.class,
          MissingOverride.class,
          NarrowingCompoundAssignment.class,
          NonAtomicVolatileUpdate.class,
          NonOverridingEquals.class,
          NullableConstructor.class,
          NullablePrimitive.class,
          NullableVoid.class,
          OperatorPrecedence.class,
          OverridesGuiceInjectableMethod.class,
          PreconditionsInvalidPlaceholder.class,
          ProtoFieldPreconditionsCheckNotNull.class,
          ReferenceEquality.class,
          RequiredModifiersChecker.class,
          StaticGuardedByInstance.class,
          SynchronizeOnNonFinalField.class,
          TruthConstantAsserts.class,
          TruthSelfEquals.class,
          TypeParameterUnusedInFormals.class,
          UnsynchronizedOverridesSynchronized.class,
          WaitNotInLoop.class);

  /** A list of all checks that are off by default. */
  public static final ImmutableSet<BugCheckerInfo> DISABLED_CHECKS =
      getSuppliers(
          ArgumentParameterMismatch.class,
          ArgumentParameterSwap.class,
          AutoFactoryAtInject.class,
          AssertFalse.class,
          AssistedInjectAndInjectOnConstructors.class,
          AssistedInjectAndInjectOnSameConstructor.class,
          BigDecimalLiteralDouble.class,
          BindingToUnqualifiedCommonType.class,
          ClassName.class,
          ComparisonContractViolated.class,
          ConstantField.class,
          DepAnn.class,
          DivZero.class,
          EmptyIfStatement.class,
          EmptySetMultibindingContributions.class,
          EmptyTopLevelDeclaration.class,
          HardCodedSdCardPath.class,
          InjectedConstructorAnnotations.class,
          InvalidTargetingOnScopingAnnotation.class,
          JMockTestWithoutRunWithOrRuleAnnotation.class,
          JavaxInjectOnFinalField.class,
          LockMethodChecker.class,
          LongLiteralLowerCaseSuffix.class,
          MixedArrayDimensions.class,
          MoreThanOneQualifier.class,
          MultiVariableDeclaration.class,
          MultibindsInsteadOfMultibindings.class,
          MultipleTopLevelClasses.class,
          NoAllocationChecker.class,
          NonCanonicalStaticMemberImport.class,
          NonRuntimeAnnotation.class,
          NumericEquality.class,
          PackageLocation.class,
          PrimitiveArrayPassedToVarargsMethod.class,
          PrivateConstructorForUtilityClass.class,
          PrivateConstructorForNoninstantiableModule.class,
          ProtoStringFieldReferenceEquality.class,
          QualifierOnMethodWithoutProvides.class,
          QualifierWithTypeUse.class,
          RedundantThrows.class,
          RemoveUnusedImports.class,
          ScopeAnnotationOnInterfaceOrAbstractClass.class,
          ScopeOrQualifierAnnotationRetention.class,
          StaticAccessedFromInstance.class,
          StaticOrDefaultInterfaceMethod.class,
          StringEquality.class,
          ThrowsUncheckedException.class,
          UnlockMethodChecker.class,
          UnnecessaryStaticImport.class,
          UseBinds.class,
          VarChecker.class,
          WildcardImport.class,
          WrongParameterPackage.class);

  // May not be instantiated
  private BuiltInCheckerSuppliers() {}
}
