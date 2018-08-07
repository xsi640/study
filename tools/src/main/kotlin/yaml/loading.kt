package yaml

import org.yaml.snakeyaml.Yaml

fun main(args: Array<String>) {
    val yaml = Yaml()
    val document = "\n- Hesperiidae\n- Papilionidae\n- Apatelodidae\n- Epiplemidae"
    val list = yaml.load<Any>(document) as List<String>
    println(list)
}