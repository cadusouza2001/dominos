import java.io.File
import com.google.common.collect.Sets
fun main() {
    val filePath = System.getProperty("user.dir")+"""\src\main\kotlin\entrada.txt"""

    val file = File(filePath)
    var n = file.readLines()[0].toInt()
    var m = 0
    while(n!=0){
        val dominos = Array(n){IntArray(2)}
        for(i in 1..n){
            val line = file.readLines()[m+i].split(" ")
            dominos[i-1][0] = line[0].toInt()
            dominos[i-1][1] = line[1].toInt()
        }
        println(encontraSetsDeMesmaSoma(dominos))
        m+=n+1
        n=file.readLines()[m].toInt()
    }
}

fun encontraSetsDeMesmaSoma(dominos: Array<IntArray>): String {
    val time = System.currentTimeMillis()
    var diferencas = Array(dominos.size){IntArray(2)}

    for (i in 0 until dominos.size){
        diferencas[i][0] = dominos[i][0] - dominos[i][1]
        diferencas[i][1] = i
    }

    var melhorSolucao = MutableList(0){IntArray(2)}
    var maiorSoma = 0
    var dominoDescartado = IntArray(2)




    for (L in (diferencas.size-1) until (diferencas.size+1)){
        var combinacoes : Set<Set<IntArray>> = Sets.combinations(diferencas.toSet(), L)
        for(combinacao in combinacoes){
            var somaAbsoluta = 0
            for(valor in combinacao){
                somaAbsoluta+=Math.abs(valor[0])
            }
            if(somaAbsoluta %2 == 0){
                var solucaoSets : Array<MutableList<IntArray>>
                solucaoSets=particaoPseudoPolinomial(combinacao)
                if(!solucaoSets[0].isEmpty() && !solucaoSets[1].isEmpty()){
                    //println("Solucoes: ${solucaoSets[0].joinToString()} e ${solucaoSets[1]}")
                    var soma = 0
                    var solucao = MutableList(0){IntArray(2)}
                    var solucaoIndices= mutableListOf<Int>()
                    for(valor in solucaoSets[0]){
                        solucaoIndices.add(valor[1])
                        if(valor[0]>=0){
                            solucao.add(dominos[valor[1]])
                        }
                        else{
                            solucao.add(intArrayOf(dominos[valor[1]][1], dominos[valor[1]][0]))
                        }
                    }
                    for(valor in solucaoSets[1]){
                        solucaoIndices.add(valor[1])
                        if(valor[0]>=0){
                            solucao.add(intArrayOf(dominos[valor[1]][1], dominos[valor[1]][0]))
                        }
                        else{
                            solucao.add(dominos[valor[1]])
                        }
                    }
                    for(valor in solucao){
                        soma+=valor[0]
                    }
                    if(soma>maiorSoma){
                        maiorSoma=soma
                        melhorSolucao=solucao
                        for (diferenca in diferencas){
                            if(!solucaoIndices.contains(diferenca[1])){
                                dominoDescartado=dominos[diferenca[1]]
                                break
                            }
                            else{
                                dominoDescartado= intArrayOf(0,0)
                            }
                        }
                    }
                }
            }
         }
     }

    var resultado=""
    if(melhorSolucao.isEmpty()) {
        resultado = "impossível"
    }
    else{
        resultado = maiorSoma.toString()+" "
        if(dominoDescartado[0] + dominoDescartado[1] != 0){
            if(dominoDescartado[0]>dominoDescartado[1]){
                resultado+="descartado o dominó " + dominoDescartado[1].toString()+" "+dominoDescartado[0].toString()
            }
            else{
                resultado+="descartado o dominó " + dominoDescartado[0].toString()+" "+dominoDescartado[1].toString()
            }
        }
        else{
            resultado+="nenhum dominó descartado"
        }
    }
    println("\nTempo para encontrar solução: ${System.currentTimeMillis()-time} ms")
    return resultado
}

fun particaoPseudoPolinomial(set: Set<IntArray>): Array<MutableList<IntArray>> {
    val valoresSet = set.map { Math.abs(it[0]) }
    val soma = valoresSet.sum()/2
    var matriz = Array(valoresSet.size+1){BooleanArray(soma+1)}
    for(i in 0..valoresSet.size){
            matriz[i][0] = true
        }
    for(i in 1..soma){
        matriz[0][i] = false
    }
    for(i in 1..valoresSet.size){
        for(j in 1..soma){
            if(j<valoresSet.elementAt(i-1)){
                matriz[i][j] = matriz[i-1][j]
            }else{
                matriz[i][j] = matriz[i-1][j] || matriz[i-1][j-valoresSet.elementAt(i-1)]
            }
        }
    }
    var set1 = MutableList(0){IntArray(2)}
    var set2 = MutableList(0){IntArray(2)}
    if(matriz[valoresSet.size][soma]) {

        var i = valoresSet.size
        var somaAtual = soma
        while (i > 0 && somaAtual >= 0) {
            if (matriz[i - 1][somaAtual]) {
                i--
                set1.add(set.elementAt(i))
            } else {
                if (matriz[i - 1][somaAtual - valoresSet.elementAt(i - 1)]) {
                    i--
                    somaAtual -= valoresSet.elementAt(i)
                    set2.add(set.elementAt(i))
                }
            }
        }
    }

    return arrayOf(set1, set2)
}


