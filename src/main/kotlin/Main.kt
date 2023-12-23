import kotlin.random.Random
import kotlin.math.*
interface Transport
{
    var speed: Double
    var price: Double
    var accidentRate: Double
    fun totalTime(c1: City, c2: City):Double
    {
        return c1.dist(c2) / speed
    }
    fun totalCost(c1: City, c2: City):Double
    {
        return c1.dist(c2) * price
    }
}
class Car:Transport
{
    override var speed = 60.0
    override var price = 200.0
    override var accidentRate = 0.01
}
class Train:Transport
{
    override var speed = 50.0
    override var price = 170.0
    override var accidentRate = 0.001
}
class Avia:Transport
{
    override var speed = 600.0
    override var price = 1000.0
    override var accidentRate = 0.0001
}
class City(var size: Int,
           var x: Double = Random.nextDouble(0.0, 1000.0),
           var y: Double = Random.nextDouble(0.0, 1000.0))
{
    fun weather(): Boolean
    {
        return Random.nextBoolean()
    }

    fun dist(c: City):Double
    {
        return sqrt((x - c.x).pow(2.0) + (y - c.y).pow(2.0))
    }
}

class Client(var c1: City, var c2: City, var weight: Double)
{
    var time: Int = 0
    var cost: Double = 0.0
    var priority = 1
    constructor(c1: City, c2: City, weight: Double, _time: Int):this(c1, c2, weight)
    {
        time = _time
        priority = 1
    }
    constructor(c1: City, c2: City, weight: Double, _cost:Double):this(c1, c2, weight)
    {
        cost = _cost
        priority = 0
    }

    fun order():Array<Any> {
        var totalTime : Double = 0.0
        var totalCost : Double = 0.0
        var car = Car()
        var train = Train()
        var avia = Avia()
        var weather_c1 = c1.weather()
        var weather_c2 = c2.weather()
        var transport: Transport = car
        if(c1.size == 0 || c2.size == 0)
        {
            totalTime = car.totalTime(c1, c2)
            totalCost = car.totalCost(c1, c2)
            transport = car
        }
        else{
            if(c1.size == 1 || c2.size == 1)
            {
                if(priority == 1)
                {
                    if(train.totalTime(c1, c2) < time)
                    {
                        totalTime = train.totalTime(c1, c2)
                        totalCost = train.totalCost(c1, c2)
                        transport = train
                    }
                    else
                    {
                        totalTime = car.totalTime(c1, c2)
                        totalCost = car.totalCost(c1, c2)
                        transport = car
                    }
                }
                else
                {
                    if(car.totalCost(c1, c2) < cost)
                    {
                        totalTime = car.totalTime(c1, c2)
                        totalCost = car.totalCost(c1, c2)
                        transport = car
                    }
                    else
                    {
                        totalTime = train.totalTime(c1, c2)
                        totalCost = train.totalCost(c1, c2)
                        transport = train
                    }
                }
            }
            else
            {
                if (priority == 1)
                {
                    if (train.totalTime(c1, c2) < time)
                    {
                        totalTime = train.totalTime(c1, c2)
                        totalCost = train.totalCost(c1, c2)
                        transport = train
                    }
                    else if (car.totalTime(c1, c2) < time)
                    {
                        totalTime = car.totalTime(c1, c2)
                        totalCost = car.totalCost(c1, c2)
                        transport = car
                    }
                    else if (weather_c1 && weather_c2)
                    {
                        totalTime = avia.totalTime(c1, c2)
                        totalCost = avia.totalCost(c1, c2)
                        transport = avia
                    }
                    else
                    {
                        totalTime = car.totalTime(c1, c2)
                        totalCost = car.totalCost(c1, c2)
                        transport = car
                    }
                }
                else
                {
                    if(avia.totalCost(c1, c2) < cost)
                    {
                        if(weather_c1 && weather_c2)
                        {
                            totalTime = avia.totalTime(c1, c2)
                            totalCost = avia.totalCost(c1, c2)
                            transport = avia
                        }
                        else if(car.totalCost(c1, c2) < cost)
                        {
                            totalTime = car.totalTime(c1, c2)
                            totalCost = car.totalCost(c1, c2)
                            transport = car
                        }
                        else
                        {
                            totalTime = train.totalTime(c1, c2)
                            totalCost = train.totalCost(c1, c2)
                            transport = train
                        }
                    }
                }
            }
        }
        var r = Random.nextDouble(0.0,1.0)
        if (r < transport.accidentRate)
        {
            Transagent.balance -= 2 * totalCost  * weight
            totalCost = 0.0
            totalTime = 0.0
        }
        else
        {
        Transagent.balance = Transagent.balance + totalCost * weight
        Transagent.time += totalTime
        Transagent.count += 1
        }
        return arrayOf(totalCost, totalTime, transport.toString())
    }
}
class Transagent()
{
    companion object
    {
        var balance = 0.0
        var time = 0.0
        var count = 0
        fun showInfo()
        {
            println("Balance: $balance, average time: ${time / count}, count: $count")
        }
    }
}
fun main(){
    var cities = Array(6, {City(Random.nextInt(0,3))})

    var clients = arrayOf(
        Client(cities[0], cities[1], 100.0),
        Client (cities[2], cities[3], 10.0, 25000000.0),
        Client (cities[4], cities[5], 10000.1, 10000000)
    )

    for (cl in clients)
    {
       var clinf = cl.order()
        println("cost ${clinf[0]}")
        println("time ${clinf[1]}")
        println("transport ${clinf[2]}")
    }

    Transagent.showInfo()
}