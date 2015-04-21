import json
import httplib
import numpy as np
import copy

global players, games
players = None
games = None

def read_db():
    global players, games

    if not (players == None or games == None):
        return (np.array(players), np.array(games))

    print "Calling Webservice"
    conn = httplib.HTTPConnection('dper.de', 9898)
    conn.connect()
    request = conn.putrequest('GET', '/dumpdb/')
    conn.endheaders()
    
    conn.send('')
    
    response = conn.getresponse().read()
    
    players = json.loads(response)['players']
    games = json.loads(response)['games']
    return (players, games)
    

def create_stats(data_cut = -1):
    global players, games
    if players == None or games == None:
        players, games = read_db()

    players_stats = copy.deepcopy(players)

    for game in games[:data_cut]:
        duration = game['timestamp_end'] - game['timestamp_start']
    
        if duration > 100:
            for player in players_stats:
                player.setdefault('goals', 1)
                player.setdefault('counter_goals', 1)
                player.setdefault('seconds_played', 1)
    
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                   player['goals'] += game['score_red'] 
                   player['counter_goals'] += game['score_black'] 
                   player['seconds_played'] += duration
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                   player['goals'] += game['score_black'] 
                   player['counter_goals'] += game['score_red'] 
                   player['seconds_played'] += duration

    for player in players_stats:
        player['goals_per_second'] = player['goals'] / float(player['seconds_played'])
        player['counter_goals_per_second'] = player['counter_goals'] / float(player['seconds_played'])
        player['effectivity'] = player['goals_per_second'] / player['counter_goals_per_second']
        player['goal_ratio'] = player['goals'] / float(player['counter_goals'])

    return (np.array(players_stats), np.array(games))

