package com.karasu256.vcnative.domain.usecase

import com.karasu256.vcnative.data.model.ExampleModel
import com.karasu256.vcnative.data.model.Resource
import com.karasu256.vcnative.data.repository.ExampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Domain-layer use case that exposes the list of example items to the
 * presentation layer, keeping view models decoupled from [ExampleRepository].
 */
class GetExamplesUseCase @Inject constructor(
    private val exampleRepository: ExampleRepository,
) {
    operator fun invoke(): Flow<Resource<List<ExampleModel>>> = exampleRepository.getExamples()
}
