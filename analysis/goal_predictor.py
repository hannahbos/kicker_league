import tools
import numpy as np
from matplotlib import pyplot as plt

class Team:
    player0 = None
    player1 = None
    offensive_strength_by_time = None
    defensive_strength_by_time = None
    strength_by_goals = None

    def add_player(self, player):
        if self.player0 == None:
            self.player0 = player
        else:
            self.player1 = player

            self.offensive_strength_by_time = np.sqrt(self.player0['goals_per_second'] * self.player1['goals_per_second'])
            self.defensive_strength_by_time = np.sqrt(self.player0['counter_goals_per_second'] * self.player1['counter_goals_per_second'])
            self.strength_by_goals = np.sqrt(self.player0['goal_ratio'] * self.player1['goal_ratio'])


def print_statistics():
    players, games = tools.create_stats()
    
    gpss = [p['goals_per_second'] for p in players]
    gpss_sorted_i = np.argsort(gpss)
    print "goals per second\n", np.array([[p['name'], p['goals_per_second']] for p in reversed(players[gpss_sorted_i])])
    
    
    cgpss = [p['counter_goals_per_second'] for p in players]
    cgpss_sorted_i = np.argsort(cgpss)
    print "counter goals per second \n", np.array([[p['name'], p['counter_goals_per_second']] for p in players[cgpss_sorted_i]])
    

    effectivitys = [p['effectivity'] for p in players]
    effectivitys_sorted_i = np.argsort(effectivitys)
    print "most effecive\n", np.array([[p['name'], p['effectivity']] for p in reversed(players[effectivitys_sorted_i])])
    

    ratios = [p['goal_ratio'] for p in players]
    ratios_sorted_i = np.argsort(ratios)
    print "best ratio\n", np.array([[p['name'], p['goal_ratio']] for p in reversed(players[ratios_sorted_i])])


def predict_goals_by_effictivity(data_cut = 100, plot = True):
    players, _ = tools.create_stats(data_cut)
    _, games = tools.read_db()

    predictions = []
    results = []

    for game in games[data_cut:]:
        team_red = Team()
        team_black = Team()

        for player in players:
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                    team_red.add_player(player)
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                    team_black.add_player(player)
        
        effectivity_red = team_red.offensive_strength_by_time # * team_black.defensive_strength_by_time
        effectivity_black = team_black.offensive_strength_by_time # * team_red.defensive_strength_by_time

        if effectivity_red > effectivity_black:
            predicted_score_black = effectivity_black / effectivity_red * 6 

            predictions.append(6 - predicted_score_black)

            if plot:
                print "Prediction: red wins 6:" + str(int(round(predicted_score_black))), " real outcome: ", str(game['score_red']) + ":" + str(game['score_black'])  
        else:
            predicted_score_red = effectivity_red / effectivity_black * 6 

            predictions.append(predicted_score_red - 6)

            if plot:
                print "Prediction: black wins 6:" + str(int(round(predicted_score_red))), " real outcome: ", str(game['score_black']) + ":" + str(game['score_red'])

        results.append(game['score_red'] - game['score_black'])

    predictions = np.array(predictions)
    results = np.array(results)
    MAE = np.mean(np.abs(predictions - results))
    if plot:
        print MAE
    return MAE



def predict_goals_by_ratio(data_cut = 100, plot = True):
    players, _ = tools.create_stats(data_cut)
    _, games = tools.read_db()

    predictions = []
    results = []

    for game in games[data_cut:]:
        team_red = Team()
        team_black = Team()

        for player in players:
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                    team_red.add_player(player)
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                    team_black.add_player(player)
        

        if team_red.strength_by_goals > team_black.strength_by_goals:
            predicted_score_black = team_black.strength_by_goals / team_red.strength_by_goals * 6 

            predictions.append(6 - predicted_score_black)

            if plot:
                print "Prediction: red wins 6:" + str(int(round(predicted_score_black))), " real outcome: ", str(game['score_red']) + ":" + str(game['score_black'])  
        else:
            predicted_score_red = team_red.strength_by_goals / team_black.strength_by_goals * 6 

            predictions.append(predicted_score_red - 6)

            if plot:
                print "Prediction: black wins 6:" + str(int(round(predicted_score_red))), " real outcome: ", str(game['score_black']) + ":" + str(game['score_red'])

        results.append(game['score_red'] - game['score_black'])

    predictions = np.array(predictions)
    results = np.array(results)
    MAE = np.mean(np.abs(predictions - results))
    if plot:
        print MAE
    return MAE


def predict_random_goals(data_cut = 100, plot = True):
    players, _ = tools.create_stats(data_cut)
    _, games = tools.read_db()

    predictions = []
    results = []

    for game in games[data_cut:]:
        team_red = Team()
        team_black = Team()

        for player in players:
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                    team_red.add_player(player)
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                    team_black.add_player(player)
        
        if np.random.rand() > 0.5:
            predictions.append(6 - np.random.randint(0,6))
        else:
            predictions.append(np.random.randint(0,6) - 6)

        if (game['score_red'] > game['score_black']):
            results.append(1)
        else:
            results.append(0)

    predictions = np.array(predictions)
    results = np.array(results)
    MAE = np.mean(np.abs(predictions - results))
    if plot:
        print MAE
    return MAE




def predict_wins_by_effectivity(data_cut = 100, plot = True):
    players, _ = tools.create_stats(data_cut)
    _, games = tools.read_db()

    predictions = []
    results = []

    for game in games[data_cut:]:
        team_red = Team()
        team_black = Team()

        for player in players:
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                    team_red.add_player(player)
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                    team_black.add_player(player)
        

        probability = team_red.offensive_strength_by_time / (team_red.offensive_strength_by_time + team_black.offensive_strength_by_time)
        predictions.append(probability)

        if (game['score_red'] > game['score_black']):
            results.append(1)
        else:
            results.append(0)

    predictions = np.array(predictions)
    results = np.array(results)
    MAE = np.mean(np.abs(predictions - results))
    if plot:
        print MAE
    return MAE



def predict_wins_by_ratio(data_cut = 100, plot = True):
    players, _ = tools.create_stats(data_cut)
    _, games = tools.read_db()

    predictions = []
    results = []

    for game in games[data_cut:]:
        team_red = Team()
        team_black = Team()

        for player in players:
                if player['id'] == game['red0'] or player['id'] == game['red1']:
                    team_red.add_player(player)
    
                if player['id'] == game['black0'] or player['id'] == game['black1']:
                    team_black.add_player(player)
        

        probability = team_red.strength_by_goals / (team_red.strength_by_goals + team_black.strength_by_goals)
        predictions.append(probability)

        if (game['score_red'] > game['score_black']):
            results.append(1)
        else:
            results.append(0)

    predictions = np.array(predictions)
    results = np.array(results)
    MAE = np.mean(np.abs(predictions - results))
    if plot:
        print MAE
    return MAE


MAEs_goals_eff = []
MAEs_goals_ratio = []
MAEs_rand_goals = []

MAEs_wins_eff = []
MAEs_wins_ratio = []

for data_cut in np.arange(1, 170, 1):
    MAE_goals_eff = predict_goals_by_effictivity(data_cut, False)
    MAE_goals_ratio = predict_goals_by_ratio(data_cut, False)
    MAE_rand_goals = predict_random_goals(data_cut, False)
    MAE_wins_eff = predict_wins_by_effectivity(data_cut, False)
    MAE_wins_ratio = predict_wins_by_ratio(data_cut, False)

    MAEs_goals_eff.append(MAE_goals_eff)
    MAEs_goals_ratio.append(MAE_goals_ratio)
    MAEs_rand_goals.append(MAE_rand_goals)
    MAEs_wins_eff.append(MAE_wins_eff)
    MAEs_wins_ratio.append(MAE_wins_ratio)

plt.figure(0)
plt.plot(MAEs_goals_eff, 'black')
plt.plot(MAEs_goals_ratio, 'blue')
plt.plot(MAEs_rand_goals, 'red')
plt.figure(1)
plt.plot(MAEs_wins_eff, 'red')
plt.plot(MAEs_wins_ratio, 'orange')
plt.show()


predict_goals_by_ratio()
print_statistics()


