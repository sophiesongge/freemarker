/*
 * Copyright 2014 Attila Szegedi, Daniel Dekany, Jonathan Revusky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package freemarker.core;

import freemarker.template.TemplateException;

/**
 * A subclass of {@link TemplateException} that says that an FTL expression has evaluated to {@code null} or it refers
 * to something that doesn't exist. At least in FreeMarker 2.3.x these two cases aren't distinguished.
 */
public class InvalidReferenceException extends TemplateException {

    static final InvalidReferenceException FAST_INSTANCE = new InvalidReferenceException(
            "Invalid reference. Details are unavilable, as this should have been handled by an FTL construct. "
            + "If it wasn't, that's problably a bug in FreeMarker.",
            null);
    
    private static final String[] TIP = new String[] {
        "If the failing expression is known to be legally refer to something that's null or missing, either specify a "
        + "default value like myOptionalVar!myDefault, or use ",
        "<#if myOptionalVar??>", "when-present", "<#else>", "when-missing", "</#if>",
        ". (These only cover the last step of the expression; to cover the whole expression, "
        + "use parenthessis: (myOptionVar.foo)!myDefault, (myOptionVar.foo)??"
    };

    private static final String TIP_NO_DOLAR =
            "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.";

    private static final String TIP_LAST_STEP_DOT =
            "It's the step after the last dot that caused this error, not those before it.";

    private static final String TIP_LAST_STEP_SQUARE_BRACKET =
            "It's the final [] step that caused this error, not those before it.";
    
    public InvalidReferenceException(Environment env) {
        super("Invalid reference: The expression has evaluated to null or refers to something that doesn't exist.",
                env);
    }

    public InvalidReferenceException(String description, Environment env) {
        super(description, env);
    }

    InvalidReferenceException(_ErrorDescriptionBuilder description, Environment env) {
        super(null, env, description, true);
    }

    /**
     * Use this whenever possible, as it returns {@link #FAST_INSTANCE} instead of creating a new instance, when
     * appropriate.
     */
    static InvalidReferenceException getInstance(Expression blamed, Environment env) {
        if (env != null && env.getFastInvalidReferenceExceptions()) {
            return FAST_INSTANCE;
        } else {
            if (blamed != null) {
                final _ErrorDescriptionBuilder errDescBuilder
                        = new _ErrorDescriptionBuilder("The following has evaluated to null or missing:").blame(blamed);
                if (endsWithDollarVariable(blamed)) {
                    errDescBuilder.tips(new Object[] { TIP_NO_DOLAR, TIP });
                } else if (blamed instanceof Dot) {
                    final String rho = ((Dot) blamed).getRHO();
                    String nameFixTip = null;
                    if ("size".equals(rho)) {
                        nameFixTip = "To query the size of a collection or map use ?size, like myList?size";
                    } else if ("length".equals(rho)) {
                        nameFixTip = "To query the length of a string use ?length, like myString?size";
                    }
                    errDescBuilder.tips(
                            nameFixTip == null
                                    ? new Object[] { TIP_LAST_STEP_DOT, TIP }
                                    : new Object[] { TIP_LAST_STEP_DOT, nameFixTip, TIP });
                } else if (blamed instanceof DynamicKeyName) {
                    errDescBuilder.tips(new Object[] { TIP_LAST_STEP_SQUARE_BRACKET, TIP });
                } else {
                    errDescBuilder.tip(TIP);
                }
                return new InvalidReferenceException(errDescBuilder, env);
            } else {
                return new InvalidReferenceException(env);
            }
        }
    }

    private static boolean endsWithDollarVariable(Expression blame) {
        return blame instanceof Identifier && ((Identifier) blame).getName().startsWith("$")
                || blame instanceof Dot && ((Dot) blame).getRHO().startsWith("$");
    }
    
}
