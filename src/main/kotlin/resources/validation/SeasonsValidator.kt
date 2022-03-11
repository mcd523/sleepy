package resources.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class SeasonsValidator: ConstraintValidator<ValidSeasons, List<String>> {
    override fun isValid(value: List<String>?, context: ConstraintValidatorContext?): Boolean {
        TODO("Not yet implemented")
    }
}
