package br.com.orange.talents

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.orange.talents")
		.start()
}

