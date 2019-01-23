package main

import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val crawler = crawler.Crawler()
    crawler.main(null)
}
