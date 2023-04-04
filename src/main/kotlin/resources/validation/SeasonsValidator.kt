package resources.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class SeasonsValidator: ConstraintValidator<ValidSeasons, List<String>> {
    override fun isValid(value: List<String>?, context: ConstraintValidatorContext?): Boolean {
        TODO("Not yet implemented")
    }
}
