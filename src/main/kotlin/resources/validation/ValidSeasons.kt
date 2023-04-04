package resources.validation

import javax.validation.Constraint

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention
@Constraint(validatedBy = [SeasonsValidator::class])
annotation class ValidSeasons(
    val message: String = "\${validatedValue} is not a valid Sleeper season.", // Final message to client is preceeded by indicator of invalid param name such as "path param type "
)
