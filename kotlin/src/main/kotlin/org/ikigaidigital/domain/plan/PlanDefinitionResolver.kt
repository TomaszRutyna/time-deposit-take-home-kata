package org.ikigaidigital.domain.plan

class PlanDefinitionResolver(private val definitions: List<BasePlanDefinition>) {
    fun resolvePlanDefinition(planType: String) =
        definitions.firstOrNull { it.planType.value.equals(planType, true) }
}