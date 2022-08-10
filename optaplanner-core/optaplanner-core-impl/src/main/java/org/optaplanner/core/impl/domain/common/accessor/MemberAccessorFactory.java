package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorFactory;

public final class MemberAccessorFactory {

    // exists only so that the various member accessors can share the same text in their exception messages
    static final String CLASSLOADER_NUDGE_MESSAGE = "Maybe add getClass().getClassLoader() as a parameter to the " +
            SolverFactory.class.getSimpleName() + ".create...() method call.";

    /**
     * Creates a new member accessor based on the given parameters.
     *
     * @param member never null, method or field to access
     * @param memberAccessorType
     * @param annotationClass the annotation the member was annotated with (used for error reporting)
     * @param domainAccessType
     * @return never null, new instance of the member accessor
     */
    public static MemberAccessor buildMemberAccessor(Member member, MemberAccessorType memberAccessorType,
            Class<? extends Annotation> annotationClass, DomainAccessType domainAccessType) {
        switch (domainAccessType) {
            case GIZMO:
                return GizmoMemberAccessorFactory.buildGizmoMemberAccessor(member, annotationClass);
            case REFLECTION:
                return buildReflectiveMemberAccessor(member, memberAccessorType, annotationClass);
            default:
                throw new IllegalStateException("The domainAccessType (" + domainAccessType + ") is not implemented.");
        }
    }

    private static MemberAccessor buildReflectiveMemberAccessor(Member member, MemberAccessorType memberAccessorType,
            Class<? extends Annotation> annotationClass) {
        if (member instanceof Field) {
            Field field = (Field) member;
            return new ReflectionFieldMemberAccessor(field);
        } else if (member instanceof Method) {
            Method method = (Method) member;
            MemberAccessor memberAccessor;
            switch (memberAccessorType) {
                case FIELD_OR_READ_METHOD:
                    if (!ReflectionHelper.isGetterMethod(method)) {
                        ReflectionHelper.assertReadMethod(method, annotationClass);
                        memberAccessor = new ReflectionMethodMemberAccessor(method);
                        break;
                    }
                    // Intentionally fall through (no break)
                case FIELD_OR_GETTER_METHOD:
                case FIELD_OR_GETTER_METHOD_WITH_SETTER:
                    boolean getterOnly = memberAccessorType != MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER;
                    ReflectionHelper.assertGetterMethod(method, annotationClass);
                    if (Modifier.isPublic(method.getModifiers())
                            // HACK The lambda approach doesn't support classes from another classloader (such as loaded by KieContainer) in JDK 8
                            // TODO In JDK 9 use MethodHandles.privateLookupIn(Class, MethodHandles.lookup())
                            && method.getDeclaringClass().getClassLoader().equals(MemberAccessor.class.getClassLoader())) {
                        memberAccessor = new LambdaBeanPropertyMemberAccessor(method, getterOnly);
                    } else {
                        memberAccessor = new ReflectionBeanPropertyMemberAccessor(method, getterOnly);
                    }
                    break;
                default:
                    throw new IllegalStateException("The memberAccessorType (" + memberAccessorType
                            + ") is not implemented.");
            }
            if (memberAccessorType == MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER
                    && !memberAccessor.supportSetter()) {
                throw new IllegalStateException("The class (" + method.getDeclaringClass()
                        + ") has a @" + annotationClass.getSimpleName()
                        + " annotated getter method (" + method
                        + "), but lacks a setter for that property (" + memberAccessor.getName() + ").");
            }
            return memberAccessor;
        } else {
            throw new IllegalStateException("Impossible state: the member (" + member + ")'s type is not a "
                    + Field.class.getSimpleName() + " or a " + Method.class.getSimpleName() + ".");
        }
    }

    private final Map<String, MemberAccessor> memberAccessorCache;

    public MemberAccessorFactory() {
        this(null);
    }

    /**
     * Prefills the member accessor cache.
     *
     * @param memberAccessorMap key is the fully qualified member name
     */
    public MemberAccessorFactory(Map<String, MemberAccessor> memberAccessorMap) {
        // The MemberAccessorFactory may be accessed, and this cache both read and updated, by multiple threads.
        this.memberAccessorCache =
                memberAccessorMap == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(memberAccessorMap);
    }

    /**
     * Creates a new member accessor based on the given parameters. Caches the result.
     *
     * @param member never null, method or field to access
     * @param memberAccessorType
     * @param annotationClass the annotation the member was annotated with (used for error reporting)
     * @param domainAccessType
     * @return never null, new {@link MemberAccessor} instance unless already found in memberAccessorMap
     */
    public MemberAccessor buildAndCacheMemberAccessor(Member member, MemberAccessorType memberAccessorType,
            Class<? extends Annotation> annotationClass, DomainAccessType domainAccessType) {
        String generatedClassName = GizmoMemberAccessorFactory.getGeneratedClassName(member);
        return memberAccessorCache.computeIfAbsent(generatedClassName,
                k -> MemberAccessorFactory.buildMemberAccessor(member, memberAccessorType, annotationClass, domainAccessType));
    }

    public enum MemberAccessorType {
        FIELD_OR_READ_METHOD,
        FIELD_OR_GETTER_METHOD,
        FIELD_OR_GETTER_METHOD_WITH_SETTER
    }
}
