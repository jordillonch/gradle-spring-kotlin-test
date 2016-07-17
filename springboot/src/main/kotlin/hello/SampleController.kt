package hello

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@EnableAutoConfiguration
class SampleController {

    @RequestMapping("/")
    @ResponseBody
    internal fun home(): String {
        return "Hello Worldddd!"
    }

    companion object {

        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(SampleController::class.java, *args)
        }
    }
}
