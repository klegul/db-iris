# db-iris
API wrapper for the Deutsche Bahn (DB) timetable API using the iris xml format. It returns information about all 
journeys passing through (departures and arrivals) a given station in a given time frame with live delay and text 
information.

# Using the API
Currently, there are two hosts for the API. There is the official host from the DB API Marketplace
(https://iris.noncd.db.de/iris-tts/timetable) and one from the
departure monitor website (https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1).

## Getting the timetable
You need the EVA station number and a date.

First create the `DBTimetables` class using the constructor. If you want to use the official API pass the clientId and 
apiKey as arguments:
```KOTLIN
val dbTimetables = DBTimetables("CLIENT_ID", "API_KEY")
```

If you want to use the unofficial api don't pass any arguments:
```KOTLIN
val dbTimetables = DBTimetables()
```

Now you can use the api:
```KOTLIN
val timetable = dbTimetables.getTimetable("8000191", LocalDateTime.now())

println("Departures from " + timetable.station)

for (journey in timetable.journeys) {
    if (journey.departureTime == null) continue
    println(journey.departureTime.toString() + " - " + (journey.trainType?.name ?: "UNK") + " " +
            journey.number + " on platform " + journey.platform)
}
```

# Understanding iris format
This is a rough guide to understanding the iris format and is not complete. For more complete document the official
OpenAPI specification provided by Deutsche Bahn in the DB API Marketplace.
([Non-static link](https://developers.deutschebahn.com/071b30a6-b030-4234-bc03-91440048d214))
This guide should provide are more readable version of the specification with explanation of some DB internal things.

The name for this format is not official and is choosing because of the url.

There are two endpoints to get all the information. The first endpoint target state of the timetable and the second
the current state with delays and information messages. 

## Plan
Let us first focus target state endpoint which from here on we call simple plan.

An example response looks something like this:

```XML
<timetable station="Karlsruhe Hbf" eva="8000191">

<s id="1299917288606705699-2304081716-12">
    <tl f="F" t="p" o="80" c="ICE" n="273"/>
    <ar pt="2304082309" pp="2" ppth="Berlin Gesundbrunnen|Berlin Hbf (tief)|Berlin Südkreuz|Lutherstadt Wittenberg Hbf|Leipzig Hbf|Erfurt Hbf|Eisenach|Fulda|Hanau Hbf|Frankfurt(Main)Hbf|Mannheim Hbf"/>
    <dp pt="2304082312" pp="2" ppth="Baden-Baden|Offenburg|Freiburg(Breisgau) Hbf|Basel Bad Bf|Basel SBB"/>
</s>

</timetable>
```

The root of the xml document is called `timetable` and contains the station name and the EVA number. The EVA number 
uniquely identifies a stop inside the DB network. A journey is represented using the `s` element which stands for stop.
A journey has an `id` attribute which is important for the target state endpoint. 

The journey contains a `tl` element which stands for trip label. It has the attribute `f` which represents the distance 
class of the trip. This can be the following: `F`- long distance trains, `N` short distance trains, 
`S` inner city/community trains. The attribute `t` stands for trip type. It can be `e, p, z, s, h, n` but most likely
you will see `p`. The attribute `o` stands for owner and contains a number identifying the company operating the 
train (Eisenbahnverkehrsunternehmen, EVU). The attribute `c` stands for category which most call the type of train.
Commonly seen categories include `CE, IC, EC, IRE, RE, RB, S, MEX, TGV, NJ, Bus`. The attribute `n` stands for the 
train number. 

The journey also contains the `ar` element which stands for arrival and the `dp` element which stand for departure. Both
element can have the same attributes. Let's call this type of element event. The event contains the attribute `pt` which
is a timestamp in the format `yyMMddHHmm`. For example `2304082309` is the 8th of April 2023 at 23:09. All timestamps
are in local time. The attribute `pp` contains the platform of the event. Tha attribute `l` stands for line an contains
the name of the line and train will run. This is usually the name most travelers will recognise like for example `S32`.
The attribute `ppath` describes the route before or after the event. Each station is separated by a `|`. For an arrival
all station before the event are listed, for a departure all station after the event are listed.

In this example we can now read that a long distance train called the `ICE 273` will arrive at `Karlsruhe Hbf` at
23:09 on platform 2 and leave at 23:12.

## Change
Now let us look at the current state endpoint which from here on we call simple change.

An example response looks something like this:

```XML
<timetable station="Karlsruhe Hbf" eva="8000191">
    
<s id="1299917288606705699-2304081716-12" eva="8000191">
    <ar ct="2304082310" cp="3" cpth="Berlin Gesundbrunnen|Berlin Hbf (tief)|Flughafen BER - Terminal 1-2|Lutherstadt Wittenberg Hbf|Leipzig Hbf|Erfurt Hbf|Eisenach|Fulda|Hanau Hbf|Frankfurt(Main)Hbf|Mannheim Hbf">
        <m id="r147209575" t="d" c="51" ts="2304081625" ts-tts="23-04-08 16:25:36.977"/>
    </ar>
    <dp ct="2304082314" cp="3">
        <m id="r1909998" t="h" from="2304080000" to="2304082359" cat="Information" ts="2303301305" ts-tts="23-04-07 23:16:09.564" pr="2"/>    </dp>
</s>

</timetable>
```

We have the same root `timetable` element as before which the same attributes. It contains a stop `s` which now also has
the EVA number as the attribute `eva`. The `id` attribute can be paired which the id found in the plan the connect 
changes to a journey. Only journeys this messages and/or delays will show up in this change document.

The stop contains the element `ar` and `dp` which are known from above but now contain different element and attributes.
An event can have the attribute `ct` which stands for changed time and is a timestamp of the actual or estimated time 
of the event. The attribute `cp` stands for changed platform and contains the new platform. The attribute `cpth` stands
for changed path and contains the new route before and after this event. The attribute `cs` stands for changed status
and is either `P` - planned when event was planned and is also used when cancellation of an event is revoked, `A` - 
added when event was added to planned data (new stop), `C` cancelled when vent was canceled which can also apply to 
planned and added stops.

And event can contain multiple `m` elements which are messages. A message can be ever type of information relevant for
the traveler. This includes things like delay reason, equipment changes and defects and ticket and regulatory
information. A message has the attribute `id` which is a unique number for the message. The attribute `t` stands for
type and describes which internal system has produced this message. A list off all can be found in `Model.kt`. The
attribute `c` stands for the code of the message. Because the message or not typed be hand every time the message is
most likely displayed via the text associated with the code. A list off all codes and their German text can also be
found in `Model.tk`. The attribute `cat` stands for category and contains the German display text for the category of
this message. The attribute `pr` stands for priority and is used is deciding which message should be displayed first
to the user. The categories are number strings: 1 `HIGHEST`, 2 `MEDIUM`, 3 `LOW`, 4 `DONE`. The attribute `ts`
represents the timestamp the message was created. There could also be the attributes `from` and `to` which also contain
a timestamp and describe a time range the message is valid.