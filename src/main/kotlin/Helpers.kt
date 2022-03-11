import kotlinx.coroutines.*

fun <A, B>Iterable<A>.pmap(f: suspend (A) -> B): List<B> {
    return runBlocking(Dispatchers.Default) {
        map { async { f(it) } }.awaitAll()
    }
}

suspend fun <A, B>Iterable<A>.pmapSuspend(f: suspend (A) -> B): List<B> {
    return withContext(Dispatchers.IO) {
        map { async { f(it) } }.awaitAll()
    }
}
